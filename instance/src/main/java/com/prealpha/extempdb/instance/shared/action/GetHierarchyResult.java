/*
 * GetHierarchyResult.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.shared.action;

import static com.google.common.base.Preconditions.*;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import com.prealpha.dispatch.shared.Result;

public class GetHierarchyResult implements Result {
	private HashMultimap<String, String> hierarchy;

	// serialization support
	@SuppressWarnings("unused")
	private GetHierarchyResult() {
	}

	public GetHierarchyResult(Multimap<String, String> hierarchy) {
		checkNotNull(hierarchy);
		this.hierarchy = HashMultimap.create(hierarchy);
	}

	public SetMultimap<String, String> getHierarchy() {
		return Multimaps.unmodifiableSetMultimap(hierarchy);
	}
}
