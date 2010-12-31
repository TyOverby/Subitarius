/*
 * Searcher.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server;

import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.prealpha.extempdb.server.domain.Source;
import com.prealpha.extempdb.server.domain.Tag;
import com.prealpha.extempdb.server.persistence.SourceDao;
import com.prealpha.extempdb.server.persistence.TagDao;
import com.prealpha.extempdb.server.persistence.Transactional;
import com.prealpha.extempdb.server.search.SearchProvider;
import com.prealpha.extempdb.server.search.SearchUnavailableException;

class Searcher implements Runnable {
	@InjectLogger
	private Logger log;

	private final SearchProvider searchProvider;

	private final SourceDao sourceDao;

	private final TagDao tagDao;

	private final Provider<SearchState> stateProvider;

	@Inject
	public Searcher(SearchProvider searchProvider, SourceDao sourceDao,
			TagDao tagDao, Provider<SearchState> stateProvider) {
		this.searchProvider = searchProvider;
		this.sourceDao = sourceDao;
		this.tagDao = tagDao;
		this.stateProvider = stateProvider;
	}

	@Override
	public void run() {
		log.info("starting search");

		Iterable<Source> allSources = getAllSources();
		Iterable<Tag> allTags = getAllTags();

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
	protected Iterable<Source> getAllSources() {
		return sourceDao.getAll();
	}

	@Transactional
	protected Iterable<Tag> getAllTags() {
		return tagDao.getAll();
	}

	@Transactional
	protected void doSearch(Tag tag, Source source)
			throws SearchUnavailableException {
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
	}
}
