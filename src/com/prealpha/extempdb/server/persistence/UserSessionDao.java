/*
 * UserSessionDao.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server.persistence;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.google.inject.Inject;
import com.prealpha.extempdb.server.domain.User;
import com.prealpha.extempdb.server.domain.UserSession;
import com.prealpha.extempdb.server.util.Base64Coder;
import com.prealpha.extempdb.server.util.Hasher;
import com.prealpha.extempdb.shared.id.UserSessionToken;

public class UserSessionDao extends GenericDao<UserSession, String> {
	private final Random random;
	
	private final Hasher hasher;

	@Inject
	public UserSessionDao(Random random, Hasher hasher) {
		this.random = random;
		this.hasher = hasher;
	}

	@Override
	protected Class<UserSession> getEntityClass() {
		return UserSession.class;
	}

	public UserSession getByUser(User user) {
		Session session = sessionProvider.get();
		Date now = new Date();

		UserSession userSession = (UserSession) session
				.createCriteria(UserSession.class)
				.add(Restrictions.eq("user", user))
				.add(Restrictions.gt("expiry", now)).uniqueResult();
		return userSession;
	}

	public UserSession createSession(User user) {
		UserSession session = new UserSession();
		session.setUser(user);

		byte[] bytes = new byte[32];
		random.nextBytes(bytes);
		byte[] hash = hasher.hash(bytes);
		String token = new String(Base64Coder.encode(hash));
		session.setToken(token);

		Calendar expiry = Calendar.getInstance();
		expiry.add(Calendar.WEEK_OF_YEAR, 2);
		session.setExpiry(expiry.getTime());

		save(session);

		return session;
	}

	public UserSession validateSession(UserSessionToken sessionToken) {
		UserSession session = get(sessionToken.getToken());

		if (!isValid(session)) {
			if (session != null) {
				delete(session);
			}

			return null;
		} else {
			return session;
		}
	}

	public static boolean isValid(UserSession session) {
		final Date now = new Date();

		if (session == null) {
			return false;
		} else if (now.compareTo(session.getExpiry()) >= 0) {
			return false;
		} else {
			return true;
		}
	}
}
