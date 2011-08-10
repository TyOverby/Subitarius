/*
 * GetMappingResult.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.shared.action;

import com.prealpha.dispatch.shared.Result;
import com.prealpha.extempdb.instance.shared.dto.TagMappingDto;

public class GetMappingResult implements Result {
	private TagMappingDto mapping;

	// serialization support
	@SuppressWarnings("unused")
	private GetMappingResult() {
	}

	public GetMappingResult(TagMappingDto mapping) {
		this.mapping = mapping;
	}

	public TagMappingDto getMapping() {
		return mapping;
	}
}
