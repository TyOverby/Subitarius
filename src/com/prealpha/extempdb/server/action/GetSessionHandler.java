/*
 * GetSessionHandler.java
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
import com.prealpha.extempdb.server.domain.UserSession;
import com.prealpha.extempdb.server.persistence.Transactional;
import com.prealpha.extempdb.server.persistence.UserSessionDao;
import com.prealpha.extempdb.shared.action.GetSession;
import com.prealpha.extempdb.shared.action.GetSessionResult;
import com.prealpha.extempdb.shared.dto.UserSessionDto;
import com.prealpha.extempdb.shared.id.UserSessionToken;

class GetSessionHandler implements ActionHandler<GetSession, GetSessionResult> {
	@InjectLogger
	private Logger log;

	private final UserSessionDao userSessionDao;

	private final Mapper mapper;

	@Inject
	public GetSessionHandler(UserSessionDao userSessionDao, Mapper mapper) {
		this.userSessionDao = userSessionDao;
		this.mapper = mapper;
	}

	@Transactional
	@Override
	public GetSessionResult execute(GetSession action, Dispatcher dispatcher)
			throws ActionException {
		UserSessionToken sessionToken = action.getSessionToken();
		UserSession session = userSessionDao.validateSession(sessionToken);

		if (session == null) {
			log.info("handled request for non-existent session, token \"{}\"",
					sessionToken.getToken());
			return new GetSessionResult(null);
		} else {
			log.info("handled request for session, token \"{}\", user \"{}\"",
					sessionToken.getToken(), session.getUser().getName());
			UserSessionDto dto = mapper.map(session, UserSessionDto.class);
			return new GetSessionResult(dto);
		}
	}
}
