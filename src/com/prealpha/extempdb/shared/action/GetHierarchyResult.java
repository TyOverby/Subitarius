/*
 * GetHierarchyResult.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.shared.action;

import static com.google.common.base.Preconditions.*;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import com.prealpha.dispatch.shared.Result;
import com.prealpha.extempdb.shared.id.TagName;

public class GetHierarchyResult implements Result {
	private HashMultimap<TagName, TagName> hierarchy;

	// serialization support
	@SuppressWarnings("unused")
	private GetHierarchyResult() {
	}

	public GetHierarchyResult(Multimap<TagName, TagName> hierarchy) {
		checkNotNull(hierarchy);
		this.hierarchy = HashMultimap.create(hierarchy);
	}

	public SetMultimap<TagName, TagName> getHierarchy() {
		return Multimaps.unmodifiableSetMultimap(hierarchy);
	}
}
