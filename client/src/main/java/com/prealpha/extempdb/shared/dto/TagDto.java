/*
 * TagDto.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.shared.dto;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.user.client.rpc.IsSerializable;

/*
 * Note that hashCode() and equals() ignore the tag name's case.
 */
public class TagDto implements IsSerializable {
	private String name;

	private boolean searched;

	private HashSet<TagDto> parents;

	public TagDto() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isSearched() {
		return searched;
	}

	public void setSearched(boolean searched) {
		this.searched = searched;
	}

	public Set<TagDto> getParents() {
		return parents;
	}

	public void setParents(Set<TagDto> parents) {
		this.parents = new HashSet<TagDto>(parents);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((name == null) ? 0 : name.toUpperCase().hashCode());
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
		if (!(obj instanceof TagDto)) {
			return false;
		}
		TagDto other = (TagDto) obj;
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
		String parentString = "";
		for (TagDto parent : parents) {
			parentString += parent.getName() + ',';
		}
		parentString = parentString.substring(0, parentString.length() - 1);

		return "TagDto [name=" + name + ", searched=" + searched + ", parents="
				+ parentString + "]";
	}
}
