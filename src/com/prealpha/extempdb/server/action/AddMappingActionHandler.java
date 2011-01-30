/*
 * AddMappingActionHandler.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server.action;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;

import com.google.inject.Inject;
import com.prealpha.dispatch.server.ActionHandler;
import com.prealpha.dispatch.shared.ActionException;
import com.prealpha.dispatch.shared.Dispatcher;
import com.prealpha.extempdb.server.InjectLogger;
import com.prealpha.extempdb.server.domain.TagMapping;
import com.prealpha.extempdb.server.domain.TagMappingAction;
import com.prealpha.extempdb.server.domain.TagMappingAction.Type;
import com.prealpha.extempdb.server.domain.User;
import com.prealpha.extempdb.shared.action.AddMappingAction;
import com.prealpha.extempdb.shared.action.MutationResult;
import com.wideplay.warp.persist.Transactional;

class AddMappingActionHandler implements
		ActionHandler<AddMappingAction, MutationResult> {
	@InjectLogger
	private Logger log;

	private final EntityManager entityManager;

	private final HttpSession httpSession;

	@Inject
	public AddMappingActionHandler(EntityManager entityManager,
			HttpSession httpSession) {
		this.entityManager = entityManager;
		this.httpSession = httpSession;
	}

	@Transactional
	@Override
	public MutationResult execute(AddMappingAction action, Dispatcher dispatcher)
			throws ActionException {
		User user = (User) httpSession.getAttribute("user");
		Long mappingId = action.getMappingId();

		if (user == null) {
			log.info(
					"denied permission to patrol/delete on mapping ID {} (not logged in)",
					mappingId);
			return MutationResult.PERMISSION_DENIED;
		}

		TagMapping mapping = entityManager.find(TagMapping.class, mappingId);
		TagMappingAction mappingAction = new TagMappingAction();
		Type type = Type.valueOf(action.getType().name());
		mappingAction.setMapping(mapping);
		mappingAction.setType(type);
		mappingAction.setUser(user);
		mappingAction.setTimestamp(new Date());
		entityManager.persist(mappingAction);

		String userName = user.getName();
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
