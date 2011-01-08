/*
 * LogInHandler.java
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
import com.prealpha.extempdb.server.domain.User;
import com.prealpha.extempdb.server.domain.UserSession;
import com.prealpha.extempdb.server.persistence.Transactional;
import com.prealpha.extempdb.server.persistence.UserDao;
import com.prealpha.extempdb.server.persistence.UserSessionDao;
import com.prealpha.extempdb.server.util.BCrypt;
import com.prealpha.extempdb.shared.action.GetSessionResult;
import com.prealpha.extempdb.shared.action.LogIn;
import com.prealpha.extempdb.shared.dto.UserSessionDto;

class LogInHandler implements ActionHandler<LogIn, GetSessionResult> {
	@InjectLogger
	private Logger log;

	private final UserDao userDao;

	private final UserSessionDao userSessionDao;

	private final Mapper mapper;

	@Inject
	public LogInHandler(UserDao userDao, UserSessionDao userSessionDao,
			Mapper mapper) {
		this.userDao = userDao;
		this.userSessionDao = userSessionDao;
		this.mapper = mapper;
	}

	@Transactional
	@Override
	public GetSessionResult execute(LogIn action, Dispatcher dispatcher)
			throws ActionException {
		String name = action.getName();
		String password = action.getPassword();

		User user = userDao.get(name);
		if (user == null) {
			log.info("denied login for non-existent user \"{}\"", name);
			return new GetSessionResult(null);
		}

		String hash = user.getHash();
		if (BCrypt.checkpw(password, hash)) {
			UserSession session = userSessionDao.createSession(user);
			log.info("successful login for user \"{}\", token \"{}\"", name,
					session.getToken());
			UserSessionDto dto = mapper.map(session, UserSessionDto.class);
			return new GetSessionResult(dto);
		} else {
			log.info("denied login for user \"{}\"; invalid password", name);
			return new GetSessionResult(null);
		}
	}
}
