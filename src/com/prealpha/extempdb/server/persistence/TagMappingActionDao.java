/*
 * TagMappingActionDao.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server.persistence;

import com.prealpha.extempdb.server.domain.TagMappingAction;

public class TagMappingActionDao extends GenericDao<TagMappingAction, Long> {
	public TagMappingActionDao() {
	}

	@Override
	protected Class<TagMappingAction> getEntityClass() {
		return TagMappingAction.class;
	}
}
