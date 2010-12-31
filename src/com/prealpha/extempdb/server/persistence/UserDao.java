/*
 * UserDao.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server.persistence;

import com.prealpha.extempdb.server.domain.User;

public class UserDao extends GenericDao<User, String> {
	@Override
	protected Class<User> getEntityClass() {
		return User.class;
	}
}
