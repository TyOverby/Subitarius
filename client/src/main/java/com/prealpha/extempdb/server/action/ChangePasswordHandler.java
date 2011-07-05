/*
 * ChangePasswordHandler.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server.action;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import com.prealpha.dispatch.server.ActionHandler;
import com.prealpha.dispatch.shared.ActionException;
import com.prealpha.dispatch.shared.Dispatcher;
import com.prealpha.extempdb.domain.User;
import com.prealpha.extempdb.server.InjectLogger;
import com.prealpha.extempdb.shared.action.ChangePassword;
import com.prealpha.extempdb.shared.action.MutationResult;

class ChangePasswordHandler implements
		ActionHandler<ChangePassword, MutationResult> {
	private static final int BCRYPT_ROUNDS = 12;

	@InjectLogger
	private Logger log;

	private final EntityManager entityManager;

	private final HttpSession httpSession;

	@Inject
	public ChangePasswordHandler(EntityManager entityManager,
			HttpSession httpSession) {
		this.entityManager = entityManager;
		this.httpSession = httpSession;
	}

	@Transactional
	@Override
	public MutationResult execute(ChangePassword action, Dispatcher dispatcher)
			throws ActionException {
		User user = (User) httpSession.getAttribute("user");

		if (user == null) {
			log.info("denied permission to change passwords (not logged in)");
			return MutationResult.PERMISSION_DENIED;
		}

		String currentPassword = action.getCurrentPassword();
		String newPassword = action.getNewPassword();

		if (newPassword == null || newPassword.isEmpty()) {
			log.info("rejected a password change request because the requested password was empty or null");
			return MutationResult.INVALID_REQUEST;
		}

		String currentHash = user.getHash();

		if (BCrypt.checkpw(currentPassword, currentHash)) {
			String salt = BCrypt.gensalt(BCRYPT_ROUNDS);
			String newHash = BCrypt.hashpw(newPassword, salt);
			user.setHash(newHash);
			entityManager.persist(entityManager.merge(user));

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
