/*
 * AddMappingAction.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.shared.action;

import static com.google.common.base.Preconditions.*;

import com.prealpha.dispatch.shared.Action;
import com.prealpha.extempdb.instance.shared.dto.TagMappingActionDto.Type;
import com.prealpha.extempdb.instance.shared.dto.TagMappingDto;

public class AddMappingAction implements Action<MutationResult> {
	private TagMappingDto.Key mappingKey;

	private Type type;

	// serialization support
	@SuppressWarnings("unused")
	private AddMappingAction() {
	}

	public AddMappingAction(TagMappingDto.Key mappingKey,
			Type type) {
		checkNotNull(mappingKey);
		checkNotNull(type);
		this.mappingKey = mappingKey;
		this.type = type;
	}

	public TagMappingDto.Key getMappingKey() {
		return mappingKey;
	}

	public Type getType() {
		return type;
	}
}
