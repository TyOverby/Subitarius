/*
 * GetHierarchyHandler.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server.action;

import org.slf4j.Logger;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import com.prealpha.extempdb.server.InjectLogger;
import com.prealpha.extempdb.server.domain.Tag;
import com.prealpha.extempdb.server.persistence.TagDao;
import com.prealpha.extempdb.server.persistence.Transactional;
import com.prealpha.extempdb.shared.action.GetHierarchy;
import com.prealpha.extempdb.shared.action.GetHierarchyResult;
import com.prealpha.extempdb.shared.id.TagName;
import com.prealpha.gwt.dispatch.server.ActionHandler;
import com.prealpha.gwt.dispatch.shared.ActionException;
import com.prealpha.gwt.dispatch.shared.Dispatcher;

class GetHierarchyHandler implements
		ActionHandler<GetHierarchy, GetHierarchyResult> {
	@InjectLogger
	private Logger log;

	private final TagDao tagDao;

	@Inject
	public GetHierarchyHandler(TagDao tagDao) {
		this.tagDao = tagDao;
	}

	@Override
	public Class<GetHierarchy> getActionType() {
		return GetHierarchy.class;
	}

	@Transactional
	@Override
	public GetHierarchyResult execute(GetHierarchy action, Dispatcher dispatcher)
			throws ActionException {
		Multimap<TagName, TagName> hierarchy = HashMultimap.create();

		for (Tag tag : tagDao.getAll()) {
			TagName name = new TagName(tag.getName());

			if (tag.getParents().isEmpty()) {
				hierarchy.put(null, name);
			} else {
				for (Tag parent : tag.getParents()) {
					TagName parentName = new TagName(parent.getName());
					hierarchy.put(parentName, name);
				}
			}
		}

		log.info("returned hierarchy on request");

		return new GetHierarchyResult(hierarchy);
	}
}
