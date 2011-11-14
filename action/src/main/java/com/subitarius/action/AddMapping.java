/*
 * AddMapping.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.action;

import static com.google.common.base.Preconditions.*;

import com.prealpha.xylophone.shared.Action;
import com.subitarius.action.dto.TagMappingDto.State;

public final class AddMapping implements Action<MutationResult> {
	private String tagName;

	private String articleUrlHash;

	private State state;

	// serialization support
	@SuppressWarnings("unused")
	private AddMapping() {
	}

	public AddMapping(String tagName, String articleUrlHash, State state) {
		checkNotNull(tagName);
		checkNotNull(articleUrlHash);
		checkNotNull(state);
		this.tagName = tagName;
		this.articleUrlHash = articleUrlHash;
		this.state = state;
	}

	public String getTagName() {
		return tagName;
	}

	public String getArticleUrlHash() {
		return articleUrlHash;
	}

	public State getState() {
		return state;
	}
}
