/*
 * GetTagSuggestionsResult.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.action;

import static com.google.common.base.Preconditions.*;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.prealpha.xylophone.shared.Result;

public final class GetTagSuggestionsResult implements Result {
	private ImmutableSet<String> suggestions;

	// serialization support
	@SuppressWarnings("unused")
	private GetTagSuggestionsResult() {
	}

	public GetTagSuggestionsResult(Set<String> suggestions) {
		checkNotNull(suggestions);
		this.suggestions = ImmutableSet.copyOf(suggestions);
	}

	public Set<String> getSuggestions() {
		return suggestions;
	}
}
