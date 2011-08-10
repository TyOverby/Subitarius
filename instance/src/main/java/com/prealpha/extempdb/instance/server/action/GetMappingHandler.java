/*
 * GetMappingHandler.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.server.action;

import javax.persistence.EntityManager;

import org.dozer.Mapper;
import org.slf4j.Logger;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import com.prealpha.dispatch.server.ActionHandler;
import com.prealpha.dispatch.shared.ActionException;
import com.prealpha.dispatch.shared.Dispatcher;
import com.prealpha.extempdb.domain.TagMapping;
import com.prealpha.extempdb.instance.shared.action.GetMapping;
import com.prealpha.extempdb.instance.shared.action.GetMappingResult;
import com.prealpha.extempdb.instance.shared.dto.TagMappingDto;
import com.prealpha.extempdb.util.logging.InjectLogger;

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
		TagMappingDto.Key dtoKey = action.getMappingKey();
		TagMapping.Key mappingKey = mapper.map(dtoKey, TagMapping.Key.class);
		TagMapping mapping = entityManager.find(TagMapping.class, mappingKey);

		if (mapping == null) {
			log.info("handled request for non-existent tag mapping, key {}",
					mappingKey);
		} else {
			log.info("handled request for tag mapping, key {}", mappingKey);
		}
		TagMappingDto mappingDto = mapper.map(mapping, TagMappingDto.class);
		return new GetMappingResult(mappingDto);
	}
}
