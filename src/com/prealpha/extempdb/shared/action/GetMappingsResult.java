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
import com.prealpha.extempdb.shared.dto.TagMappingDto;

public class GetMappingsResult implements Result {
	private ArrayList<TagMappingDto.Key> mappingKeys;

	// serialization support
	@SuppressWarnings("unused")
	private GetMappingsResult() {
	}

	public GetMappingsResult(List<TagMappingDto.Key> mappingKeys) {
		checkNotNull(mappingKeys);
		this.mappingKeys = new ArrayList<TagMappingDto.Key>(mappingKeys);
	}

	public List<TagMappingDto.Key> getMappingKeys() {
		return Collections.unmodifiableList(mappingKeys);
	}
}
