/*
 * GetMappingResult.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.action;

import com.prealpha.xylophone.shared.Result;
import com.subitarius.action.dto.TagMappingDto;

public final class GetMappingResult implements Result {
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
