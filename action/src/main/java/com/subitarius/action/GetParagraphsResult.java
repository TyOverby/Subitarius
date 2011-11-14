/*
 * GetParagraphsResult.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.action;

import static com.google.common.base.Preconditions.*;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.prealpha.xylophone.shared.Result;

public final class GetParagraphsResult implements Result {
	private ImmutableList<String> paragraphs;

	// serialization support
	@SuppressWarnings("unused")
	private GetParagraphsResult() {
	}

	public GetParagraphsResult(List<String> paragraphs) {
		checkNotNull(paragraphs);
		this.paragraphs = ImmutableList.copyOf(paragraphs);
	}

	public List<String> getParagraphs() {
		return paragraphs;
	}
}
