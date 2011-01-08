/*
 * GetTagSuggestionsResult.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.shared.action;

import static com.google.common.base.Preconditions.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.prealpha.dispatch.shared.Result;
import com.prealpha.extempdb.shared.id.TagName;

public class GetTagSuggestionsResult implements Result {
	private HashSet<TagName> suggestions;

	// serialization support
	@SuppressWarnings("unused")
	private GetTagSuggestionsResult() {
	}

	public GetTagSuggestionsResult(Set<TagName> suggestions) {
		checkNotNull(suggestions);
		this.suggestions = new HashSet<TagName>(suggestions);
	}

	public Set<TagName> getSuggestions() {
		return Collections.unmodifiableSet(suggestions);
	}
}
