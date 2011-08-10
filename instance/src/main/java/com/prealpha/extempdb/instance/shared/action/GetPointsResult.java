/*
 * GetPointsResult.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.shared.action;

import static com.google.common.base.Preconditions.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.prealpha.dispatch.shared.Result;
import com.prealpha.extempdb.shared.dto.UserDto;

public class GetPointsResult implements Result {
	private HashMap<UserDto, Integer> points;

	// serialization support
	@SuppressWarnings("unused")
	private GetPointsResult() {
	}

	public GetPointsResult(Map<UserDto, Integer> points) {
		checkNotNull(points);
		this.points = new HashMap<UserDto, Integer>(points);
	}

	public Map<UserDto, Integer> getPoints() {
		return Collections.unmodifiableMap(points);
	}
}
