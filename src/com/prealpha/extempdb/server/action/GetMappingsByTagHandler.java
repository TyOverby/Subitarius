/*
 * GetMappingsByTagHandler.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.dozer.Mapper;
import org.slf4j.Logger;

import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import com.prealpha.extempdb.server.InjectLogger;
import com.prealpha.extempdb.server.domain.Tag;
import com.prealpha.extempdb.server.domain.TagMapping;
import com.prealpha.extempdb.server.persistence.TagDao;
import com.prealpha.extempdb.server.persistence.Transactional;
import com.prealpha.extempdb.shared.action.GetMappingsByTag;
import com.prealpha.extempdb.shared.action.GetMappingsResult;
import com.prealpha.extempdb.shared.dto.TagDto;
import com.prealpha.extempdb.shared.dto.TagMappingDto;
import com.prealpha.extempdb.shared.id.TagMappingId;
import com.prealpha.gwt.dispatch.server.ActionHandler;
import com.prealpha.gwt.dispatch.shared.ActionException;
import com.prealpha.gwt.dispatch.shared.Dispatcher;

class GetMappingsByTagHandler implements
		ActionHandler<GetMappingsByTag, GetMappingsResult> {
	@InjectLogger
	private Logger log;

	private final TagDao tagDao;

	private final Mapper mapper;

	@Inject
	public GetMappingsByTagHandler(TagDao tagDao, Mapper mapper) {
		this.tagDao = tagDao;
		this.mapper = mapper;
	}

	@Override
	public Class<GetMappingsByTag> getActionType() {
		return GetMappingsByTag.class;
	}

	@Transactional
	@Override
	public GetMappingsResult execute(GetMappingsByTag action,
			Dispatcher dispatcher) throws ActionException {
		TagDto tagDto = action.getTag();
		Tag tag = tagDao.get(tagDto.getName());
		List<TagMappingDto> dtos = new ArrayList<TagMappingDto>();

		for (TagMapping mapping : tag.getMappings()) {
			dtos.add(mapper.map(mapping, TagMappingDto.class));
		}

		if (action.getComparator() != null) {
			Collections.sort(dtos, action.getComparator());
		}

		List<TagMappingId> ids = new ArrayList<TagMappingId>();

		for (TagMappingDto dto : Iterables.filter(dtos, action)) {
			ids.add(new TagMappingId(dto.getId()));
		}

		log.info(
				"handled request for mappings to tag {}, returning {} mappings",
				tag.getName(), ids.size());

		return new GetMappingsResult(ids);
	}
}
