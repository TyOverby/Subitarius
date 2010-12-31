/*
 * AddMappingActionHandler.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server.action;

import java.util.Date;

import org.dozer.Mapper;
import org.slf4j.Logger;

import com.google.inject.Inject;
import com.prealpha.extempdb.server.InjectLogger;
import com.prealpha.extempdb.server.domain.TagMappingAction;
import com.prealpha.extempdb.server.domain.UserSession;
import com.prealpha.extempdb.server.persistence.TagMappingActionDao;
import com.prealpha.extempdb.server.persistence.Transactional;
import com.prealpha.extempdb.server.persistence.UserSessionDao;
import com.prealpha.extempdb.shared.action.AddMappingAction;
import com.prealpha.extempdb.shared.action.MutationResult;
import com.prealpha.extempdb.shared.dto.TagMappingActionDto;
import com.prealpha.extempdb.shared.id.UserSessionToken;
import com.prealpha.gwt.dispatch.server.ActionHandler;
import com.prealpha.gwt.dispatch.shared.ActionException;
import com.prealpha.gwt.dispatch.shared.Dispatcher;

class AddMappingActionHandler implements
		ActionHandler<AddMappingAction, MutationResult> {
	@InjectLogger
	private Logger log;

	private final UserSessionDao userSessionDao;

	private final TagMappingActionDao tagMappingActionDao;

	private final Mapper mapper;

	@Inject
	public AddMappingActionHandler(UserSessionDao userSessionDao,
			TagMappingActionDao tagMappingActionDao, Mapper mapper) {
		this.userSessionDao = userSessionDao;
		this.tagMappingActionDao = tagMappingActionDao;
		this.mapper = mapper;
	}

	@Override
	public Class<AddMappingAction> getActionType() {
		return AddMappingAction.class;
	}

	@Transactional
	@Override
	public MutationResult execute(AddMappingAction action, Dispatcher dispatcher)
			throws ActionException {
		UserSessionToken sessionToken = action.getSessionToken();
		UserSession session = userSessionDao.validateSession(sessionToken);

		TagMappingActionDto dto = action.getMappingAction();
		Long mappingId = dto.getMapping().getId();

		if (session == null) {
			log.info(
					"rejected attempt to patrol/delete on mapping ID {} because of invalid session",
					mappingId);
			return MutationResult.INVALID_SESSION;
		}

		TagMappingAction mappingAction = mapper
				.map(dto, TagMappingAction.class);
		mappingAction.setUser(session.getUser());
		mappingAction.setTimestamp(new Date());
		tagMappingActionDao.save(mappingAction);

		String userName = session.getUser().getName();
		switch (mappingAction.getType()) {
		case PATROL:
			log.info("user \"{}\" patrolled mapping ID {}", userName, mappingId);
			break;
		case REMOVE:
			log.info("user \"{}\" removed mapping ID {}", userName, mappingId);
			break;
		default:
			throw new IllegalStateException();
		}

		return MutationResult.SUCCESS;
	}
}
