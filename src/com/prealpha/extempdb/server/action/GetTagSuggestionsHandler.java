/*
 * GetTagSuggestionsHandler.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server.action;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.slf4j.Logger;

import com.google.inject.Inject;
import com.prealpha.extempdb.server.InjectLogger;
import com.prealpha.extempdb.server.domain.Tag;
import com.prealpha.extempdb.server.persistence.TagDao;
import com.prealpha.extempdb.server.persistence.Transactional;
import com.prealpha.extempdb.shared.action.GetTagSuggestions;
import com.prealpha.extempdb.shared.action.GetTagSuggestionsResult;
import com.prealpha.extempdb.shared.id.TagName;
import com.prealpha.gwt.dispatch.server.ActionHandler;
import com.prealpha.gwt.dispatch.shared.ActionException;
import com.prealpha.gwt.dispatch.shared.Dispatcher;

class GetTagSuggestionsHandler implements
		ActionHandler<GetTagSuggestions, GetTagSuggestionsResult> {
	@InjectLogger
	private Logger log;

	private final TagDao tagDao;

	@Inject
	public GetTagSuggestionsHandler(TagDao tagDao) {
		this.tagDao = tagDao;
	}

	@Override
	public Class<GetTagSuggestions> getActionType() {
		return GetTagSuggestions.class;
	}

	@Transactional
	@Override
	public GetTagSuggestionsResult execute(GetTagSuggestions action,
			Dispatcher dispatcher) throws ActionException {
		String namePrefix = action.getNamePrefix();
		Set<TagName> suggestions = new HashSet<TagName>();

		Iterator<Tag> i1 = tagDao.getByNamePrefix(namePrefix).iterator();
		int count = 0;
		while (i1.hasNext() && count < action.getLimit()) {
			Tag tag = i1.next();
			TagName tagName = new TagName(tag.getName());
			suggestions.add(tagName);
			count++;
		}

		log.info("returned {} tag suggestions on request for prefix {}",
				suggestions.size(), namePrefix);

		return new GetTagSuggestionsResult(suggestions);
	}
}
