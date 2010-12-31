/*
 * GetMappingsResult.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.shared.action;

import static com.google.common.base.Preconditions.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.prealpha.extempdb.shared.id.TagMappingId;
import com.prealpha.gwt.dispatch.shared.Result;

public class GetMappingsResult implements Result {
	private ArrayList<TagMappingId> ids;

	// serialization support
	@SuppressWarnings("unused")
	private GetMappingsResult() {
	}

	public GetMappingsResult(List<TagMappingId> ids) {
		checkNotNull(ids);
		this.ids = new ArrayList<TagMappingId>(ids);
	}

	public List<TagMappingId> getIds() {
		return Collections.unmodifiableList(ids);
	}
}
