/*
 * AddMappingAction.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.shared.action;

import static com.google.common.base.Preconditions.*;

import com.prealpha.extempdb.shared.dto.TagMappingActionDto.Type;

public class AddMappingAction implements AuthenticatedAction<MutationResult> {
	private String sessionId;

	private Long mappingId;

	private Type type;

	// serialization support
	@SuppressWarnings("unused")
	private AddMappingAction() {
	}

	public AddMappingAction(String sessionId, Long mappingId, Type type) {
		checkNotNull(sessionId);
		checkNotNull(mappingId);
		checkNotNull(type);

		this.sessionId = sessionId;
		this.mappingId = mappingId;
		this.type = type;
	}

	@Override
	public String getSessionId() {
		return sessionId;
	}

	public Long getMappingId() {
		return mappingId;
	}

	public Type getType() {
		return type;
	}
}
