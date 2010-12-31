/*
 * AddMappingAction.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.shared.action;

import static com.google.common.base.Preconditions.*;

import com.prealpha.extempdb.shared.dto.TagMappingActionDto;
import com.prealpha.extempdb.shared.id.UserSessionToken;
import com.prealpha.gwt.dispatch.shared.Action;

public class AddMappingAction implements Action<MutationResult> {
	private TagMappingActionDto mappingAction;

	private UserSessionToken sessionToken;

	// serialization support
	@SuppressWarnings("unused")
	private AddMappingAction() {
	}

	public AddMappingAction(TagMappingActionDto mappingAction,
			UserSessionToken sessionToken) {
		checkNotNull(mappingAction);
		checkNotNull(sessionToken);

		this.mappingAction = mappingAction;
		this.sessionToken = sessionToken;
	}

	public TagMappingActionDto getMappingAction() {
		return mappingAction;
	}

	public UserSessionToken getSessionToken() {
		return sessionToken;
	}
}
