/*
 * GetTagHandler.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server.action;

import org.dozer.Mapper;
import org.slf4j.Logger;

import com.google.inject.Inject;
import com.prealpha.dispatch.server.ActionHandler;
import com.prealpha.dispatch.shared.ActionException;
import com.prealpha.dispatch.shared.Dispatcher;
import com.prealpha.extempdb.server.InjectLogger;
import com.prealpha.extempdb.server.domain.Tag;
import com.prealpha.extempdb.server.persistence.TagDao;
import com.prealpha.extempdb.server.persistence.Transactional;
import com.prealpha.extempdb.shared.action.GetTag;
import com.prealpha.extempdb.shared.action.GetTagResult;
import com.prealpha.extempdb.shared.dto.TagDto;
import com.prealpha.extempdb.shared.id.TagName;

class GetTagHandler implements ActionHandler<GetTag, GetTagResult> {
	@InjectLogger
	private Logger log;

	private final TagDao tagDao;

	private final Mapper mapper;

	@Inject
	public GetTagHandler(TagDao tagDao, Mapper mapper) {
		this.tagDao = tagDao;
		this.mapper = mapper;
	}

	@Transactional
	@Override
	public GetTagResult execute(GetTag action, Dispatcher dispatcher)
			throws ActionException {
		TagName tagName = action.getName();
		Tag tag = tagDao.get(tagName.getName());

		if (tag == null) {
			log.info("handled request for non-existent tag, name \"{}\"",
					tagName.getName());
			return new GetTagResult(null);
		} else {
			log.info("handled request for tag, name \"{}\"", tagName.getName());
			TagDto dto = mapper.map(tag, TagDto.class);
			return new GetTagResult(dto);
		}
	}
}
