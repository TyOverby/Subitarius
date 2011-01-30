/*
 * GetUserHandler.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server.action;

import javax.servlet.http.HttpSession;

import org.dozer.Mapper;
import org.slf4j.Logger;

import com.google.inject.Inject;
import com.prealpha.dispatch.server.ActionHandler;
import com.prealpha.dispatch.shared.ActionException;
import com.prealpha.dispatch.shared.Dispatcher;
import com.prealpha.extempdb.server.InjectLogger;
import com.prealpha.extempdb.server.domain.User;
import com.prealpha.extempdb.shared.action.GetUser;
import com.prealpha.extempdb.shared.action.GetUserResult;
import com.prealpha.extempdb.shared.dto.UserDto;

class GetUserHandler implements ActionHandler<GetUser, GetUserResult> {
	@InjectLogger
	private Logger log;

	private final HttpSession httpSession;

	private final Mapper mapper;

	@Inject
	public GetUserHandler(HttpSession httpSession, Mapper mapper) {
		this.httpSession = httpSession;
		this.mapper = mapper;
	}

	@Override
	public GetUserResult execute(GetUser action, Dispatcher dispatcher)
			throws ActionException {
		User user = (User) httpSession.getAttribute("user");

		if (user == null) {
			log.info("reported to client that session ID {} is not logged in",
					httpSession.getId());
			return new GetUserResult(null);
		} else {
			log.info(
					"reported to client that session ID {} is logged in as user \"{}\"",
					httpSession.getId(), user.getName());
			UserDto dto = mapper.map(user, UserDto.class);
			return new GetUserResult(dto);
		}
	}
}
