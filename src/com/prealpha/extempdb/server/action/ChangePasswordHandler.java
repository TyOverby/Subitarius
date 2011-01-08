/*
 * ChangePasswordHandler.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server.action;

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
import com.prealpha.extempdb.shared.action.ChangePassword;
import com.prealpha.extempdb.shared.action.MutationResult;
import com.prealpha.extempdb.shared.id.UserSessionToken;

class ChangePasswordHandler implements
		ActionHandler<ChangePassword, MutationResult> {
	private static final int BCRYPT_ROUNDS = 12;

	@InjectLogger
	private Logger log;

	private final UserDao userDao;

	private final UserSessionDao userSessionDao;

	@Inject
	public ChangePasswordHandler(UserDao userDao, UserSessionDao userSessionDao) {
		this.userDao = userDao;
		this.userSessionDao = userSessionDao;
	}

	@Transactional
	@Override
	public MutationResult execute(ChangePassword action, Dispatcher dispatcher)
			throws ActionException {
		UserSessionToken sessionToken = action.getSessionToken();
		UserSession session = userSessionDao.validateSession(sessionToken);

		if (session == null) {
			log.info("rejected a password change attempt due to invalid session token");
			return MutationResult.INVALID_SESSION;
		}

		String currentPassword = action.getCurrentPassword();
		String newPassword = action.getNewPassword();

		if (newPassword == null || newPassword.isEmpty()) {
			log.info("rejected a password change request because the requested password was empty or null");
			return MutationResult.INVALID_REQUEST;
		}

		User user = session.getUser();
		String currentHash = user.getHash();

		if (BCrypt.checkpw(currentPassword, currentHash)) {
			String salt = BCrypt.gensalt(BCRYPT_ROUNDS);
			String newHash = BCrypt.hashpw(newPassword, salt);
			user.setHash(newHash);
			userDao.save(user);

			log.info("changed password for user \"{}\" on request",
					user.getName());

			return MutationResult.SUCCESS;
		} else {
			log.info(
					"rejected a password change request for user \"{}\" because authentication failed",
					user.getName());
			return MutationResult.PERMISSION_DENIED;
		}
	}
}
