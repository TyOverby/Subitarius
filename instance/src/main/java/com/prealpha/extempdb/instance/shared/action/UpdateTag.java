/*
 * UpdateTag.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.shared.action;

import static com.google.common.base.Preconditions.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class UpdateTag implements AuthenticatedAction<MutationResult> {
	private String sessionId;

	private String tagName;

	private boolean searched;

	private HashSet<String> parents;

	// serialization support
	@SuppressWarnings("unused")
	private UpdateTag() {
	}

	public UpdateTag(String sessionId, String tagName, boolean searched,
			Set<String> parents) {
		checkNotNull(sessionId);
		checkNotNull(tagName);
		checkNotNull(parents);

		this.sessionId = sessionId;
		this.tagName = tagName;
		this.searched = searched;
		this.parents = new HashSet<String>(parents);
	}

	@Override
	public String getSessionId() {
		return sessionId;
	}

	public String getTagName() {
		return tagName;
	}

	public boolean isSearched() {
		return searched;
	}

	public Set<String> getParents() {
		return Collections.unmodifiableSet(parents);
	}
}
