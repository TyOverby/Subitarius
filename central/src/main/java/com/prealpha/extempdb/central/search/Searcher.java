/*
 * Searcher.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.central.search;

import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import com.prealpha.extempdb.domain.ArticleUrl;
import com.prealpha.extempdb.domain.DistributedEntity;
import com.prealpha.extempdb.domain.DistributedEntity_;
import com.prealpha.extempdb.domain.Source;
import com.prealpha.extempdb.domain.Tag;
import com.prealpha.extempdb.domain.Tag.Type;
import com.prealpha.extempdb.domain.TagMapping;
import com.prealpha.extempdb.util.logging.InjectLogger;

class Searcher {
	@InjectLogger
	private Logger log;

	private final EntityManager entityManager;

	private final SearchProvider searchProvider;

	@Inject
	private Searcher(EntityManager entityManager, SearchProvider searchProvider) {
		this.entityManager = entityManager;
		this.searchProvider = searchProvider;
	}

	public void run() {
		run(null);
	}

	public void run(Set<Integer> sourceOrdinals) {
		log.info("starting search");
		try {
			for (Source source : Source.values()) {
				if (sourceOrdinals == null
						|| sourceOrdinals.contains(source.ordinal())) {
					try {
						for (Tag tag : getAllCurrent(Tag.class)) {
							if (tag.getType() == Type.SEARCHED) {
								search(tag, source);
							}
						}
					} catch (RuntimeException rx) {
						log.error("unexpected exception was thrown", rx);
					}
				}
			}
			log.info("search complete");
		} catch (SearchUnavailableException sux) {
			log.error("search provider was unavailable", sux);
		}
	}

	@Transactional
	void search(Tag tag, Source source) throws SearchUnavailableException {
		int resultCount = 0;
		List<ArticleUrl> articleUrls = searchProvider.search(tag, source, 1);
		for (ArticleUrl articleUrl : articleUrls) {
			entityManager.persist(articleUrl);
			log.debug("persisted article URL: {}", articleUrl);

			TagMapping mapping = new TagMapping(tag, articleUrl);
			if (mappingExists(mapping)) {
				log.debug("mapping already exists: {}", mapping);
			} else {
				entityManager.persist(mapping);
				log.debug("persisted mapping: {}", mapping);
			}
		}
		Object[] args = { resultCount, tag, source };
		log.info("handled {} search result(s) for query: ({}; {})", args);
	}

	@Transactional
	<T extends DistributedEntity> List<T> getAllCurrent(Class<T> entityClass) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<T> criteria = builder.createQuery(entityClass);
		Root<T> root = criteria.from(entityClass);
		criteria.where(builder.isNull(root.get(DistributedEntity_.child)));
		criteria.distinct(true);
		return entityManager.createQuery(criteria).getResultList();
	}

	private boolean mappingExists(TagMapping mapping) {
		TagMapping existing = entityManager.find(TagMapping.class,
				mapping.getHash());
		if (existing != null) {
			return true;
		} else if (mapping.getTag().getParent() != null) {
			Tag parentTag = (Tag) mapping.getTag().getParent();
			ArticleUrl articleUrl = mapping.getArticleUrl();
			return mappingExists(new TagMapping(parentTag, articleUrl));
		} else {
			return false;
		}
	}
}
