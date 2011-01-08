/*
 * GetMappingHandler.java
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
import com.prealpha.extempdb.server.domain.TagMapping;
import com.prealpha.extempdb.server.persistence.TagMappingDao;
import com.prealpha.extempdb.server.persistence.Transactional;
import com.prealpha.extempdb.shared.action.GetMapping;
import com.prealpha.extempdb.shared.action.GetMappingResult;
import com.prealpha.extempdb.shared.dto.TagMappingDto;
import com.prealpha.extempdb.shared.id.TagMappingId;

class GetMappingHandler implements ActionHandler<GetMapping, GetMappingResult> {
	@InjectLogger
	private Logger log;

	private final TagMappingDao tagMappingDao;

	private final Mapper mapper;

	@Inject
	public GetMappingHandler(TagMappingDao tagMappingDao, Mapper mapper) {
		this.tagMappingDao = tagMappingDao;
		this.mapper = mapper;
	}

	@Transactional
	@Override
	public GetMappingResult execute(GetMapping action, Dispatcher dispatcher)
			throws ActionException {
		TagMappingId id = action.getId();
		TagMapping mapping = tagMappingDao.get(id.getId());
		if (mapping == null) {
			log.info("handled request for non-existent tag mapping, ID {}",
					id.getId());
		} else {
			log.info("handled request for tag mapping, ID {}", id.getId());
		}
		TagMappingDto mappingDto = mapper.map(mapping, TagMappingDto.class);
		return new GetMappingResult(mappingDto);
	}
}
