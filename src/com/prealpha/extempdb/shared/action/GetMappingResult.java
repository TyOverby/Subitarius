/*
 * GetMappingResult.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.shared.action;

import com.prealpha.extempdb.shared.dto.TagMappingDto;
import com.prealpha.gwt.dispatch.shared.Result;

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
