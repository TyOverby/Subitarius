/*
 * GetMappingHandler.java
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
import com.prealpha.extempdb.server.domain.TagMapping;
import com.prealpha.extempdb.shared.action.GetMapping;
import com.prealpha.extempdb.shared.action.GetMappingResult;
import com.prealpha.extempdb.shared.dto.TagMappingDto;
import com.wideplay.warp.persist.Transactional;

class GetMappingHandler implements ActionHandler<GetMapping, GetMappingResult> {
	@InjectLogger
	private Logger log;

	private final EntityManager entityManager;

	private final Mapper mapper;

	@Inject
	public GetMappingHandler(EntityManager entityManager, Mapper mapper) {
		this.entityManager = entityManager;
		this.mapper = mapper;
	}

	@Transactional
	@Override
	public GetMappingResult execute(GetMapping action, Dispatcher dispatcher)
			throws ActionException {
		Long mappingId = action.getMappingId();
		TagMapping mapping = entityManager.find(TagMapping.class, mappingId);
		if (mapping == null) {
			log.info("handled request for non-existent tag mapping, ID {}",
					mappingId);
		} else {
			log.info("handled request for tag mapping, ID {}", mappingId);
		}
		TagMappingDto mappingDto = mapper.map(mapping, TagMappingDto.class);
		return new GetMappingResult(mappingDto);
	}
}
