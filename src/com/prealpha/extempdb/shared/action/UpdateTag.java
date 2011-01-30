/*
 * UpdateTag.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.shared.action;

import static com.google.common.base.Preconditions.*;

import com.prealpha.extempdb.shared.dto.TagDto;

public class UpdateTag implements AuthenticatedAction<MutationResult> {
	public static enum UpdateType {
		SAVE, DELETE;
	}

	private String sessionId;

	private TagDto tag;

	private UpdateType updateType;

	// serialization support
	@SuppressWarnings("unused")
	private UpdateTag() {
	}

	public UpdateTag(String sessionId, TagDto tag, UpdateType updateType) {
		checkNotNull(sessionId);
		checkNotNull(tag);
		checkNotNull(updateType);

		this.sessionId = sessionId;
		this.tag = tag;
		this.updateType = updateType;
	}

	@Override
	public String getSessionId() {
		return sessionId;
	}

	public TagDto getTag() {
		return tag;
	}

	public UpdateType getUpdateType() {
		return updateType;
	}
}
