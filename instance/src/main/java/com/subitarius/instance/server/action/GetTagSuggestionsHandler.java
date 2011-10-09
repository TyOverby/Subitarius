/*
 * GetTagSuggestionsHandler.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.instance.server.action;

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
import com.subitarius.action.GetTagSuggestions;
import com.subitarius.action.GetTagSuggestionsResult;
import com.subitarius.domain.Tag;
import com.subitarius.domain.Tag_;
import com.subitarius.util.logging.InjectLogger;

final class GetTagSuggestionsHandler implements
		ActionHandler<GetTagSuggestions, GetTagSuggestionsResult> {
	@InjectLogger
	private Logger log;

	private final EntityManager entityManager;

	@Inject
	private GetTagSuggestionsHandler(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public GetTagSuggestionsResult execute(GetTagSuggestions action,
			Dispatcher dispatcher) throws ActionException {
		String namePrefix = action.getNamePrefix();
		String strictPrefix = namePrefix.trim();
		String wordPrefix = ' ' + strictPrefix;
		Set<String> suggestions = new HashSet<String>();

		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Tag> criteria = builder.createQuery(Tag.class);
		Root<Tag> tagRoot = criteria.from(Tag.class);
		Predicate strict = builder.equal(
				builder.locate(tagRoot.get(Tag_.name), strictPrefix), 1);
		Predicate word = builder.greaterThan(
				builder.locate(tagRoot.get(Tag_.name), wordPrefix), 0);
		criteria.where(builder.or(strict, word));
		List<Tag> tags = entityManager.createQuery(criteria).getResultList();

		Iterator<Tag> i1 = tags.iterator();
		int count = 0;
		while (i1.hasNext() && count < action.getLimit()) {
			Tag tag = i1.next();
			suggestions.add(tag.getName());
			count++;
		}

		log.info("returned {} tag suggestions on request for prefix \"{}\"",
				suggestions.size(), namePrefix);

		return new GetTagSuggestionsResult(suggestions);
	}
}
