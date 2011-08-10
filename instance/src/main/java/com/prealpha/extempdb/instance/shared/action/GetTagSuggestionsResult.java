/*
 * GetTagSuggestionsResult.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.shared.action;

import static com.google.common.base.Preconditions.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.prealpha.dispatch.shared.Result;

public class GetTagSuggestionsResult implements Result {
	private HashSet<String> suggestions;

	// serialization support
	@SuppressWarnings("unused")
	private GetTagSuggestionsResult() {
	}

	public GetTagSuggestionsResult(Set<String> suggestions) {
		checkNotNull(suggestions);
		this.suggestions = new HashSet<String>(suggestions);
	}

	public Set<String> getSuggestions() {
		return Collections.unmodifiableSet(suggestions);
	}
}
