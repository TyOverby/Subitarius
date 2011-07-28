/*
 * LogOutHandler.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server.action;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;

import com.google.inject.Inject;
import com.prealpha.dispatch.server.ActionHandler;
import com.prealpha.dispatch.shared.ActionException;
import com.prealpha.dispatch.shared.Dispatcher;
import com.prealpha.extempdb.domain.User;
import com.prealpha.extempdb.shared.action.LogOut;
import com.prealpha.extempdb.shared.action.MutationResult;
import com.prealpha.extempdb.util.logging.InjectLogger;

class LogOutHandler implements ActionHandler<LogOut, MutationResult> {
	@InjectLogger
	private Logger logger;

	private final HttpSession httpSession;

	@Inject
	public LogOutHandler(HttpSession httpSession) {
		this.httpSession = httpSession;
	}

	@Override
	public MutationResult execute(LogOut action, Dispatcher dispatcher)
			throws ActionException {
		User user = (User) httpSession.getAttribute("user");

		if (user == null) {
			logger.info(
					"rejected log out request; session ID {} not logged in",
					httpSession.getId());
			return MutationResult.INVALID_REQUEST;
		} else {
			httpSession.setAttribute("user", null);
			logger.info("logged out user \"{}\", session ID {}",
					user.getName(), httpSession.getId());
			return MutationResult.SUCCESS;
		}
	}
}
