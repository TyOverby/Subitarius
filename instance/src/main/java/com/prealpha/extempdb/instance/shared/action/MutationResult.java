/*
 * MutationResult.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.shared.action;

import com.prealpha.dispatch.shared.Result;

public enum MutationResult implements Result {
	SUCCESS, INVALID_REQUEST, PERMISSION_DENIED;
}
