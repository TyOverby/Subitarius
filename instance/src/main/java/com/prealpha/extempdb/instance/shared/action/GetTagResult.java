/*
 * GetTagResult.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.shared.action;

import com.prealpha.dispatch.shared.Result;
import com.prealpha.extempdb.instance.shared.dto.TagDto;

public class GetTagResult implements Result {
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
