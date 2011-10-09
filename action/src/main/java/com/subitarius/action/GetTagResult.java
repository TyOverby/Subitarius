/*
 * GetTagResult.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.action;

import com.prealpha.dispatch.shared.Result;
import com.subitarius.action.dto.TagDto;

public final class GetTagResult implements Result {
	private TagDto tag;

	// serialization support
	@SuppressWarnings("unused")
	private GetTagResult() {
	}

	public GetTagResult(TagDto tag) {
		this.tag = tag;
	}

	public TagDto getTag() {
		return tag;
	}
}
