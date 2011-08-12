/*
 * TagDto.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.shared.dto;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.user.client.rpc.IsSerializable;

/*
 * Note that hashCode() and equals() ignore the tag name's case.
 */
public class TagDto implements IsSerializable {
	public static enum Type {
		PLACEHOLDER, SEARCHED, ARCHIVED;
	}
	
	private String hash;
	
	private String name;

	private Type type;

	private HashSet<TagDto> parents;

	public TagDto() {
	}
	
	public String getHash() {
		return hash;
	}
	
	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Type getType() {
		return type;
	}
	
	public void setType(Type type) {
		this.type = type;
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
}
