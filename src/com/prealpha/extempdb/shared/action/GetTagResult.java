/*
 * GetTagResult.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.shared.action;

import com.prealpha.extempdb.shared.dto.TagDto;
import com.prealpha.gwt.dispatch.shared.Result;

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
