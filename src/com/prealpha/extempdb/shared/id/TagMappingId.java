/*
 * TagMappingId.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.shared.id;

import static com.google.common.base.Preconditions.*;

import com.google.gwt.user.client.rpc.IsSerializable;

public class TagMappingId implements IsSerializable {
	private Long id;

	// serialization support
	@SuppressWarnings("unused")
	private TagMappingId() {
	}

	public TagMappingId(Long id) {
		checkNotNull(id);
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof TagMappingId)) {
			return false;
		}
		TagMappingId other = (TagMappingId) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return id.toString();
	}
}
