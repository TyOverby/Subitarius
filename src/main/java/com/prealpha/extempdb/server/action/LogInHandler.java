/*
 * LogInHandler.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server.action;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpSession;

import org.dozer.Mapper;
import org.slf4j.Logger;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import com.prealpha.dispatch.server.ActionHandler;
import com.prealpha.dispatch.shared.ActionException;
import com.prealpha.dispatch.shared.Dispatcher;
import com.prealpha.extempdb.server.InjectLogger;
import com.prealpha.extempdb.server.domain.User;
import com.prealpha.extempdb.shared.action.GetUserResult;
import com.prealpha.extempdb.shared.action.LogIn;
import com.prealpha.extempdb.shared.dto.UserDto;

class LogInHandler implements ActionHandler<LogIn, GetUserResult> {
	@InjectLogger
	private Logger log;

	private final EntityManager entityManager;

	private final HttpSession httpSession;

	private final Mapper mapper;

	@Inject
	public LogInHandler(EntityManager entityManager, HttpSession httpSession,
			Mapper mapper) {
		this.entityManager = entityManager;
		this.httpSession = httpSession;
		this.mapper = mapper;
	}

	@Transactional
	@Override
	public GetUserResult execute(LogIn action, Dispatcher dispatcher)
			throws ActionException {
		// discard the currently logged in user for this session
		httpSession.setAttribute("user", null);

		String name = action.getName();
		String password = action.getPassword();

		User user = entityManager.find(User.class, name);
		if (user == null) {
			log.info("denied login for non-existent user \"{}\"", name);
			return new GetUserResult(null);
		}

		String hash = user.getHash();
		if (BCrypt.checkpw(password, hash)) {
			httpSession.setAttribute("user", user);
			log.info("successful login for user \"{}\", session ID {}", name,
					httpSession.getId());
			UserDto dto = mapper.map(user, UserDto.class);
			return new GetUserResult(dto);
		} else {
			log.info("denied login for user \"{}\"; invalid password", name);
			return new GetUserResult(null);
		}
	}
}
