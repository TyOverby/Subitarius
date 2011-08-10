/*
 * AddMappingActionHandler.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.server.action;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpSession;

import org.apache.catalina.User;
import org.dozer.Mapper;
import org.slf4j.Logger;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import com.prealpha.dispatch.server.ActionHandler;
import com.prealpha.dispatch.shared.ActionException;
import com.prealpha.dispatch.shared.Dispatcher;
import com.prealpha.extempdb.domain.TagMapping;
import com.prealpha.extempdb.instance.shared.action.AddMappingAction;
import com.prealpha.extempdb.instance.shared.action.MutationResult;
import com.prealpha.extempdb.instance.shared.dto.TagMappingDto;
import com.prealpha.extempdb.util.logging.InjectLogger;

class AddMappingActionHandler implements
		ActionHandler<AddMappingAction, MutationResult> {
	@InjectLogger
	private Logger log;

	private final EntityManager entityManager;

	private final HttpSession httpSession;

	private final Mapper mapper;

	@Inject
	public AddMappingActionHandler(EntityManager entityManager,
			HttpSession httpSession, Mapper mapper) {
		this.entityManager = entityManager;
		this.httpSession = httpSession;
		this.mapper = mapper;
	}

	@Transactional
	@Override
	public MutationResult execute(AddMappingAction action, Dispatcher dispatcher)
			throws ActionException {
		User user = (User) httpSession.getAttribute("user");
		TagMappingDto.Key dtoKey = action.getMappingKey();
		TagMapping.Key mappingKey = mapper.map(dtoKey, TagMapping.Key.class);

		if (user == null) {
			log.info(
					"denied permission to patrol/delete on mapping key {} (not logged in)",
					mappingKey);
			return MutationResult.PERMISSION_DENIED;
		}

		TagMapping mapping = entityManager.find(TagMapping.class, mappingKey);
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
			log.info("user \"{}\" patrolled mapping key {}", userName,
					mappingKey);
			break;
		case REMOVE:
			log.info("user \"{}\" removed mapping key {}", userName, mappingKey);
			break;
		default:
			throw new IllegalStateException();
		}

		return MutationResult.SUCCESS;
	}
}
