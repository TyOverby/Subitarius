/*
 * ActionIterator.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.launcher;

import java.util.Iterator;

import com.google.common.collect.AbstractIterator;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.prealpha.extempdb.domain.DistributedEntity;

final class ActionIterator extends AbstractIterator<Action> {
	private final Iterator<DistributedEntity> entityIterator;

	private final Provider<UserAction> context;

	@Inject
	private ActionIterator(Iterator<DistributedEntity> entityIterator,
			Provider<UserAction> context) {
		this.entityIterator = entityIterator;
		this.context = context;
	}

	@Override
	protected Action computeNext() {
		UserAction action = context.get();
		while (entityIterator.hasNext()) {
			DistributedEntity entity = entityIterator.next();
			if (action != null && !action.apply(entity)) {
				return new EntityAction(entity);
			}
		}
		return endOfData();
	}
}
