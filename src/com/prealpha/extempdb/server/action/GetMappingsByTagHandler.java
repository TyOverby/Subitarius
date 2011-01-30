/*
 * GetMappingsByTagHandler.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;

import org.dozer.Mapper;
import org.slf4j.Logger;

import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import com.prealpha.dispatch.server.ActionHandler;
import com.prealpha.dispatch.shared.ActionException;
import com.prealpha.dispatch.shared.Dispatcher;
import com.prealpha.extempdb.server.InjectLogger;
import com.prealpha.extempdb.server.domain.Tag;
import com.prealpha.extempdb.server.domain.TagMapping;
import com.prealpha.extempdb.shared.action.GetMappingsByTag;
import com.prealpha.extempdb.shared.action.GetMappingsResult;
import com.prealpha.extempdb.shared.dto.TagMappingDto;
import com.wideplay.warp.persist.Transactional;

class GetMappingsByTagHandler implements
		ActionHandler<GetMappingsByTag, GetMappingsResult> {
	@InjectLogger
	private Logger log;

	private final EntityManager entityManager;

	private final Mapper mapper;

	@Inject
	public GetMappingsByTagHandler(EntityManager entityManager, Mapper mapper) {
		this.entityManager = entityManager;
		this.mapper = mapper;
	}

	@Transactional
	@Override
	public GetMappingsResult execute(GetMappingsByTag action,
			Dispatcher dispatcher) throws ActionException {
		String tagName = action.getTagName();
		Tag tag = entityManager.find(Tag.class, tagName);
		List<TagMappingDto> dtos = new ArrayList<TagMappingDto>();

		for (TagMapping mapping : tag.getMappings()) {
			dtos.add(mapper.map(mapping, TagMappingDto.class));
		}

		if (action.getComparator() != null) {
			Collections.sort(dtos, action.getComparator());
		}

		List<Long> mappingIds = new ArrayList<Long>();

		for (TagMappingDto dto : Iterables.filter(dtos, action)) {
			mappingIds.add(dto.getId());
		}

		log.info(
				"handled request for mappings to tag {}, returning {} mappings",
				tagName, mappingIds.size());

		return new GetMappingsResult(mappingIds);
	}
}
