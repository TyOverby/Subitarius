/*
 * GetHierarchyResult.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.instance.shared.action;

import static com.google.common.base.Preconditions.*;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.prealpha.dispatch.shared.Result;

public final class GetHierarchyResult implements Result {
	private ImmutableMultimap<String, String> hierarchy;

	// serialization support
	@SuppressWarnings("unused")
	private GetHierarchyResult() {
	}

	public GetHierarchyResult(Multimap<String, String> hierarchy) {
		checkNotNull(hierarchy);
		this.hierarchy = ImmutableMultimap.copyOf(hierarchy);
	}

	public Multimap<String, String> getHierarchy() {
		return hierarchy;
	}
}
