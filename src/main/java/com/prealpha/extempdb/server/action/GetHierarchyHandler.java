/*
 * GetHierarchyHandler.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server.action;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import com.prealpha.dispatch.server.ActionHandler;
import com.prealpha.dispatch.shared.ActionException;
import com.prealpha.dispatch.shared.Dispatcher;
import com.prealpha.extempdb.server.InjectLogger;
import com.prealpha.extempdb.server.domain.Tag;
import com.prealpha.extempdb.shared.action.GetHierarchy;
import com.prealpha.extempdb.shared.action.GetHierarchyResult;

class GetHierarchyHandler implements
		ActionHandler<GetHierarchy, GetHierarchyResult> {
	@InjectLogger
	private Logger log;

	private final EntityManager entityManager;

	@Inject
	public GetHierarchyHandler(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Transactional
	@Override
	public GetHierarchyResult execute(GetHierarchy action, Dispatcher dispatcher)
			throws ActionException {
		Multimap<String, String> hierarchy = HashMultimap.create();

		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Tag> criteria = builder.createQuery(Tag.class);
		Root<Tag> tagRoot = criteria.from(Tag.class);
		criteria.select(tagRoot);
		criteria.distinct(true);
		List<Tag> tags = entityManager.createQuery(criteria).getResultList();

		for (Tag tag : tags) {
			String tagName = tag.getName();
			if (tag.getParents().isEmpty()) {
				hierarchy.put(null, tagName);
			} else {
				for (Tag parent : tag.getParents()) {
					String parentName = parent.getName();
					hierarchy.put(parentName, tagName);
				}
			}
		}

		log.info("returned hierarchy on request");

		return new GetHierarchyResult(hierarchy);
	}
}