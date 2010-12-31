/*
 * SourceDao.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server.persistence;

import com.prealpha.extempdb.server.domain.Source;

public class SourceDao extends GenericDao<Source, Long> {
	@Override
	protected Class<Source> getEntityClass() {
		return Source.class;
	}
}
