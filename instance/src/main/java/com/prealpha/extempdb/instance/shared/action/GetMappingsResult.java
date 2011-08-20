/*
 * GetMappingsResult.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.shared.action;

import static com.google.common.base.Preconditions.*;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.prealpha.dispatch.shared.Result;
import com.prealpha.extempdb.instance.shared.dto.TagMappingDto;

public final class GetMappingsResult implements Result {
	private ImmutableList<TagMappingDto> mappings;

	// serialization support
	@SuppressWarnings("unused")
	private GetMappingsResult() {
	}

	public GetMappingsResult(Iterable<TagMappingDto> mappings) {
		checkNotNull(mappings);
		this.mappings = ImmutableList.copyOf(mappings);
	}

	public List<TagMappingDto> getMappings() {
		return mappings;
	}
}
