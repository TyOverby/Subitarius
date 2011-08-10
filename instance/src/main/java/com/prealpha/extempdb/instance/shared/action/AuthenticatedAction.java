/*
 * AuthenticatedAction.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.shared.action;

import com.prealpha.dispatch.shared.Action;
import com.prealpha.dispatch.shared.Result;

public interface AuthenticatedAction<R extends Result> extends Action<R> {
	String getSessionId();
}
