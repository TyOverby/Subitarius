/*
 * AddMappingAction.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.shared.action;

import static com.google.common.base.Preconditions.*;

import com.prealpha.extempdb.instance.shared.dto.TagMappingActionDto.Type;
import com.prealpha.extempdb.instance.shared.dto.TagMappingDto;

public class AddMappingAction implements AuthenticatedAction<MutationResult> {
	private String sessionId;

	private TagMappingDto.Key mappingKey;

	private Type type;

	// serialization support
	@SuppressWarnings("unused")
	private AddMappingAction() {
	}

	public AddMappingAction(String sessionId, TagMappingDto.Key mappingKey,
			Type type) {
		checkNotNull(sessionId);
		checkNotNull(mappingKey);
		checkNotNull(type);

		this.sessionId = sessionId;
		this.mappingKey = mappingKey;
		this.type = type;
	}

	@Override
	public String getSessionId() {
		return sessionId;
	}

	public TagMappingDto.Key getMappingKey() {
		return mappingKey;
	}

	public Type getType() {
		return type;
	}
}
