/*
 * MutationResult.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.shared.action;

import com.prealpha.gwt.dispatch.shared.Result;

public enum MutationResult implements Result {
	SUCCESS, INVALID_REQUEST, INVALID_SESSION, PERMISSION_DENIED;
}
