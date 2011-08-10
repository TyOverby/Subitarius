/*
 * TagMappingActionDto.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.shared.dto;

import com.google.gwt.user.client.rpc.IsSerializable;

public class TagMappingActionDto implements IsSerializable {
	public static enum Type {
		PATROL, REMOVE;
	}

	private Long id;

	private TagMappingDto mapping;

	private Type type;

	public TagMappingActionDto() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public TagMappingDto getMapping() {
		return mapping;
	}

	public void setMapping(TagMappingDto mapping) {
		this.mapping = mapping;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}
}
