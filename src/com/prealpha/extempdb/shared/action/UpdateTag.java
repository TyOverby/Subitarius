/*
 * UpdateTag.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.shared.action;

import static com.google.common.base.Preconditions.*;

import com.prealpha.extempdb.shared.dto.TagDto;
import com.prealpha.extempdb.shared.id.UserSessionToken;
import com.prealpha.gwt.dispatch.shared.Action;

public class UpdateTag implements Action<MutationResult> {
	public static enum UpdateType {
		SAVE, DELETE;
	}

	private TagDto tag;

	private UpdateType updateType;

	private UserSessionToken sessionToken;

	// serialization support
	@SuppressWarnings("unused")
	private UpdateTag() {
	}

	public UpdateTag(TagDto tag, UpdateType updateType,
			UserSessionToken sessionToken) {
		checkNotNull(tag);
		checkNotNull(updateType);
		checkNotNull(sessionToken);

		this.tag = tag;
		this.updateType = updateType;
		this.sessionToken = sessionToken;
	}

	public TagDto getTag() {
		return tag;
	}

	public UpdateType getUpdateType() {
		return updateType;
	}

	public UserSessionToken getSessionToken() {
		return sessionToken;
	}
}
