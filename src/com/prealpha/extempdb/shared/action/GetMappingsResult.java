/*
 * GetMappingsResult.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.shared.action;

import static com.google.common.base.Preconditions.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.prealpha.dispatch.shared.Result;

public class GetMappingsResult implements Result {
	private ArrayList<Long> mappingIds;

	// serialization support
	@SuppressWarnings("unused")
	private GetMappingsResult() {
	}

	public GetMappingsResult(List<Long> mappingIds) {
		checkNotNull(mappingIds);
		this.mappingIds = new ArrayList<Long>(mappingIds);
	}

	public List<Long> getMappingIds() {
		return Collections.unmodifiableList(mappingIds);
	}
}
