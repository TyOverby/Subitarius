/*
 * GetTagHandler.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server.action;

import javax.persistence.EntityManager;

import org.dozer.Mapper;
import org.slf4j.Logger;

import com.google.inject.Inject;
import com.prealpha.dispatch.server.ActionHandler;
import com.prealpha.dispatch.shared.ActionException;
import com.prealpha.dispatch.shared.Dispatcher;
import com.prealpha.extempdb.server.InjectLogger;
import com.prealpha.extempdb.server.domain.Tag;
import com.prealpha.extempdb.shared.action.GetTag;
import com.prealpha.extempdb.shared.action.GetTagResult;
import com.prealpha.extempdb.shared.dto.TagDto;
import com.wideplay.warp.persist.Transactional;

class GetTagHandler implements ActionHandler<GetTag, GetTagResult> {
	@InjectLogger
	private Logger log;

	private final EntityManager entityManager;

	private final Mapper mapper;

	@Inject
	public GetTagHandler(EntityManager entityManager, Mapper mapper) {
		this.entityManager = entityManager;
		this.mapper = mapper;
	}

	@Transactional
	@Override
	public GetTagResult execute(GetTag action, Dispatcher dispatcher)
			throws ActionException {
		String tagName = action.getTagName();
		Tag tag = entityManager.find(Tag.class, tagName);

		if (tag == null) {
			log.info("handled request for non-existent tag, name \"{}\"",
					tagName);
			return new GetTagResult(null);
		} else {
			log.info("handled request for tag, name \"{}\"", tagName);
			TagDto dto = mapper.map(tag, TagDto.class);
			return new GetTagResult(dto);
		}
	}
}
