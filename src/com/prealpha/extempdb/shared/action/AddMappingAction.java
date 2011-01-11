/*
 * AddMappingAction.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.shared.action;

import static com.google.common.base.Preconditions.*;

import com.prealpha.dispatch.shared.Action;
import com.prealpha.extempdb.shared.dto.TagMappingActionDto.Type;
import com.prealpha.extempdb.shared.dto.TagMappingDto;
import com.prealpha.extempdb.shared.id.UserSessionToken;

public class AddMappingAction implements Action<MutationResult> {
	private TagMappingDto mapping;

	private Type type;

	private UserSessionToken sessionToken;

	// serialization support
	@SuppressWarnings("unused")
	private AddMappingAction() {
	}

	public AddMappingAction(TagMappingDto mapping, Type type,
			UserSessionToken sessionToken) {
		checkNotNull(mapping);
		checkNotNull(type);
		checkNotNull(sessionToken);

		this.mapping = mapping;
		this.type = type;
		this.sessionToken = sessionToken;
	}

	public TagMappingDto getMapping() {
		return mapping;
	}

	public Type getType() {
		return type;
	}

	public UserSessionToken getSessionToken() {
		return sessionToken;
	}
}
