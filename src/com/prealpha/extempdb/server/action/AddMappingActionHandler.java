/*
 * AddMappingActionHandler.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server.action;

import java.util.Date;

import org.slf4j.Logger;

import com.google.inject.Inject;
import com.prealpha.dispatch.server.ActionHandler;
import com.prealpha.dispatch.shared.ActionException;
import com.prealpha.dispatch.shared.Dispatcher;
import com.prealpha.extempdb.server.InjectLogger;
import com.prealpha.extempdb.server.domain.TagMapping;
import com.prealpha.extempdb.server.domain.TagMappingAction;
import com.prealpha.extempdb.server.domain.TagMappingAction.Type;
import com.prealpha.extempdb.server.domain.UserSession;
import com.prealpha.extempdb.server.persistence.TagMappingActionDao;
import com.prealpha.extempdb.server.persistence.TagMappingDao;
import com.prealpha.extempdb.server.persistence.Transactional;
import com.prealpha.extempdb.server.persistence.UserSessionDao;
import com.prealpha.extempdb.shared.action.AddMappingAction;
import com.prealpha.extempdb.shared.action.MutationResult;
import com.prealpha.extempdb.shared.dto.TagMappingDto;
import com.prealpha.extempdb.shared.id.UserSessionToken;

class AddMappingActionHandler implements
		ActionHandler<AddMappingAction, MutationResult> {
	@InjectLogger
	private Logger log;

	private final UserSessionDao userSessionDao;

	private final TagMappingDao tagMappingDao;

	private final TagMappingActionDao tagMappingActionDao;

	@Inject
	public AddMappingActionHandler(UserSessionDao userSessionDao,
			TagMappingDao tagMappingDao, TagMappingActionDao tagMappingActionDao) {
		this.userSessionDao = userSessionDao;
		this.tagMappingDao = tagMappingDao;
		this.tagMappingActionDao = tagMappingActionDao;
	}

	@Transactional
	@Override
	public MutationResult execute(AddMappingAction action, Dispatcher dispatcher)
			throws ActionException {
		UserSessionToken sessionToken = action.getSessionToken();
		UserSession session = userSessionDao.validateSession(sessionToken);

		TagMappingDto mappingDto = action.getMapping();
		Long mappingId = mappingDto.getId();

		if (session == null) {
			log.info(
					"rejected attempt to patrol/delete on mapping ID {} because of invalid session",
					mappingId);
			return MutationResult.INVALID_SESSION;
		}

		TagMapping mapping = tagMappingDao.get(mappingId);
		TagMappingAction mappingAction = new TagMappingAction();
		Type type = Type.valueOf(action.getType().name());
		mappingAction.setMapping(mapping);
		mappingAction.setType(type);
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
