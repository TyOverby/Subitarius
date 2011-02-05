/*
 * Searcher.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server;

import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.prealpha.extempdb.server.domain.Source;
import com.prealpha.extempdb.server.domain.Tag;
import com.prealpha.extempdb.server.search.SearchProvider;
import com.prealpha.extempdb.server.search.SearchUnavailableException;
import com.wideplay.warp.persist.Transactional;

class Searcher implements Runnable {
	@InjectLogger
	private Logger log;

	private final EntityManager entityManager;

	private final SearchProvider searchProvider;

	private final Provider<SearchState> stateProvider;

	@Inject
	public Searcher(EntityManager entityManager, SearchProvider searchProvider,
			Provider<SearchState> stateProvider) {
		this.entityManager = entityManager;
		this.searchProvider = searchProvider;
		this.stateProvider = stateProvider;
	}

	@Override
	public void run() {
		log.info("starting search");

		Iterable<Source> allSources = getAll(Source.class);
		Iterable<Tag> allTags = getAll(Tag.class);

		try {
			for (Source source : allSources) {
				try {
					for (Tag tag : allTags) {
						if (tag.isSearched()) {
							doSearch(tag, source);
						}
					}
				} catch (RuntimeException rx) {
					log.error("unexpected exception was thrown: ", rx);
				}
			}
			log.info("search complete");
		} catch (SearchUnavailableException sux) {
			log.error("search provider was unavailable: ", sux);
		}
	}

	@Transactional
	protected <T> Iterable<T> getAll(Class<T> entityClass) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<T> criteria = builder.createQuery(entityClass);
		Root<T> root = criteria.from(entityClass);
		criteria.select(root);
		criteria.distinct(true);
		return entityManager.createQuery(criteria).getResultList();
	}

	@Transactional
	protected void doSearch(Tag tag, Source source)
			throws SearchUnavailableException {
		try {
			List<String> urls = searchProvider.search(tag, source);
			SearchState searchState = stateProvider.get();
			searchState.init(tag, source);

			Iterator<String> i1 = urls.iterator();
			int count = urls.size();
			while (i1.hasNext() && searchState.shouldContinue(count)) {
				searchState.handle(i1.next());
			}

			Object[] params = new Object[] { searchState.getMappingCount(),
					searchState.getParseCount(), source.getDisplayName(),
					tag.getName() };
			log.info(
					"mapped {} articles (including {} newly parsed) from source \"{}\" to tag \"{}\"",
					params);
		} catch (ClassNotFoundException cnfx) {
			throw new SearchUnavailableException(cnfx);
		}
	}
}
