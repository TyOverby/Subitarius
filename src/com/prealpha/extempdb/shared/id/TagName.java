/*
 * TagName.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.shared.id;

import static com.google.common.base.Preconditions.*;

import com.google.gwt.user.client.rpc.IsSerializable;

/*
 * Note that equals() and hashCode() for this class ignore the tag name's case.
 */
public class TagName implements IsSerializable {
	private String name;

	// serialization support
	@SuppressWarnings("unused")
	private TagName() {
	}

	public TagName(String name) {
		checkNotNull(name);
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((name == null) ? 0 : name.toLowerCase().hashCode());
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
		if (!(obj instanceof TagName)) {
			return false;
		}
		TagName other = (TagName) obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equalsIgnoreCase(other.name)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return name;
	}
}
