/*
 * GetParagraphsResult.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.shared.action;

import static com.google.common.base.Preconditions.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.prealpha.dispatch.shared.Result;

public class GetParagraphsResult implements Result {
	private ArrayList<String> paragraphs;

	// serialization support
	@SuppressWarnings("unused")
	private GetParagraphsResult() {
	}

	public GetParagraphsResult(List<String> paragraphs) {
		checkNotNull(paragraphs);
		this.paragraphs = new ArrayList<String>(paragraphs);
	}

	public List<String> getParagraphs() {
		return Collections.unmodifiableList(paragraphs);
	}
}
