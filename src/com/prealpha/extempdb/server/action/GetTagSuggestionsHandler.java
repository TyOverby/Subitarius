/*
 * GetTagSuggestionsHandler.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server.action;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;

import com.google.inject.Inject;
import com.prealpha.dispatch.server.ActionHandler;
import com.prealpha.dispatch.shared.ActionException;
import com.prealpha.dispatch.shared.Dispatcher;
import com.prealpha.extempdb.server.InjectLogger;
import com.prealpha.extempdb.server.domain.Tag;
import com.prealpha.extempdb.server.domain.Tag_;
import com.prealpha.extempdb.shared.action.GetTagSuggestions;
import com.prealpha.extempdb.shared.action.GetTagSuggestionsResult;
import com.wideplay.warp.persist.Transactional;

class GetTagSuggestionsHandler implements
		ActionHandler<GetTagSuggestions, GetTagSuggestionsResult> {
	@InjectLogger
	private Logger log;

	private final EntityManager entityManager;

	@Inject
	public GetTagSuggestionsHandler(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Transactional
	@Override
	public GetTagSuggestionsResult execute(GetTagSuggestions action,
			Dispatcher dispatcher) throws ActionException {
		String namePrefix = action.getNamePrefix().trim();
		Set<String> suggestions = new HashSet<String>();

		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Tag> criteria = builder.createQuery(Tag.class);
		Root<Tag> tagRoot = criteria.from(Tag.class);
		Predicate strictPrefix = builder.equal(
				builder.locate(tagRoot.get(Tag_.name), namePrefix), 1);
		Predicate wordPrefix = builder.greaterThan(
				builder.locate(tagRoot.get(Tag_.name), ' ' + namePrefix), 0);
		criteria.where(builder.or(strictPrefix, wordPrefix));
		List<Tag> tags = entityManager.createQuery(criteria).getResultList();

		Iterator<Tag> i1 = tags.iterator();
		int count = 0;
		while (i1.hasNext() && count < action.getLimit()) {
			Tag tag = i1.next();
			suggestions.add(tag.getName());
			count++;
		}

		log.info("returned {} tag suggestions on request for prefix {}",
				suggestions.size(), namePrefix);

		return new GetTagSuggestionsResult(suggestions);
	}
}
