/*
 * UserAction.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.launcher;

import com.google.common.base.Predicate;
import com.prealpha.extempdb.domain.DistributedEntity;

public interface UserAction extends Action, Iterable<Runnable>,
		Predicate<DistributedEntity> {
	int size();
}
