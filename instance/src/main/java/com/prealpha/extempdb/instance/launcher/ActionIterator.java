/*
 * ActionIterator.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.launcher;

import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Queue;

import com.google.common.collect.AbstractIterator;
import com.google.inject.Inject;
import com.prealpha.extempdb.domain.DistributedEntity;

final class ActionIterator extends AbstractIterator<Action> {
	private final Iterator<DistributedEntity> entityIterator;

	private final UserActionContext context;

	private final Queue<Action> actions;

	@Inject
	private ActionIterator(Iterator<DistributedEntity> entityIterator,
			UserActionContext context) {
		this.entityIterator = entityIterator;
		this.context = context;
		actions = new PriorityQueue<Action>(1, new Comparator<Action>() {
			@Override
			public int compare(Action a1, Action a2) {
				return a1.getTimestamp().compareTo(a2.getTimestamp());
			}
		});

		this.context.addUserActionListener(new UserActionListener() {
			@Override
			public void onActionStart(UserAction action) {
				actions.add(action);
			}

			@Override
			public void onActionProgress(UserAction action, int complete,
					int total) {
			}

			@Override
			public void onActionComplete(UserAction action) {
			}
		});
	}

	@Override
	protected Action computeNext() {
		UserAction userAction = context.getActiveAction();
		while (entityIterator.hasNext()) {
			DistributedEntity entity = entityIterator.next();
			if (userAction != null && !userAction.apply(entity)) {
				actions.add(new EntityAction(entity));
			}
		}
		return (!actions.isEmpty() ? actions.remove() : endOfData());
	}
}
