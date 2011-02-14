/*
 * Searcher.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server.search;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;

import com.google.inject.Inject;
import com.prealpha.extempdb.server.InjectLogger;
import com.prealpha.extempdb.server.domain.Article;
import com.prealpha.extempdb.server.domain.Source;
import com.prealpha.extempdb.server.domain.Tag;
import com.prealpha.extempdb.server.domain.TagMapping;
import com.wideplay.warp.persist.Transactional;

public class Searcher implements Runnable {
	@InjectLogger
	private Logger log;
	
	private final EntityManager entityManager;

	private final SearchProvider searchProvider;

	private final ArticleProcessor articleProcessor;

	@Inject
	public Searcher(SearchProvider searchProvider, ArticleProcessor articleProcessor) {
		this.searchProvider = searchProvider;
		this.articleProcessor = articleProcessor;
	}

	@Override
	public void run() {
		log.info("starting search");
		try {
			for (Source source : getAll(Source.class)) {
				try {
					for (Tag tag : getAll(Tag.class)) {
						if (tag.isSearched()) {
							SearchQuery query = new SearchQuery(source, tag);
							execute(query);
						}
					}
				} catch (RuntimeException rx) {
					log.error("unexpected exception was thrown", rx);
				} catch (ClassNotFoundException cnfx) {
					log.error("article parser class not found", cnfx);
				}
			}
			log.info("search complete");
		} catch (SearchUnavailableException sux) {
			log.error("search provider was unavailable", sux);
		}
	}

	@Transactional
	void execute(SearchQuery query) throws SearchUnavailableException, ClassNotFoundException {
		int resultCount = 0;
		List<String> urls = searchProvider.search(query, 1);

		if (urls.isEmpty()) {
			log.info("found no results for query {}", query);
		} else {
			for (String url : urls) {
				try {
					Article article = articleProcessor.process(url, query.getSource());
					if (article != null) {
						persistIfNew(query.createTagMapping(article));
						resultCount++;
					}
				} catch (ArticleParseException apx) {
					if (apx.getCause() instanceof StatusCodeException) {
						StatusCodeException scx = (StatusCodeException) apx.getCause();
						int statusCode = scx.getStatusCode();
						log.warn("article parse failed due to HTTP status code {}, URL {}", statusCode, url);
					} else {
						log.warn("article parse failed, URL " + url + ": ", apx);
					}
				}
			}
			log.info("handled {} result(s) for query {}", resultCount, query);
		}
	}

	private void persistIfNew(TagMapping mapping) {
		TagMapping.Key mappingKey = mapping.getKey();
		TagMapping existing = entityManager.find(TagMapping.class, mappingKey);

		if (existing == null) {
			entityManager.persist(mapping);
			log.debug("mapping key {} created and persisted", mappingKey);
		} else {
			log.debug("mapping key {} already exists", mappingKey);
		}
	}

	@Transactional
	<T> List<T> getAll(Class<T> entityClass) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<T> criteria = builder.createQuery(entityClass);
		Root<T> root = criteria.from(entityClass);
		criteria.select(root);
		criteria.distinct(true);
		return entityManager.createQuery(criteria).getResultList();
	}
}
