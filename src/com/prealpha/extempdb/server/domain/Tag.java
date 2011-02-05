/*
 * Tag.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server.domain;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

/*
 * Note that hashCode() and equals() ignore the tag name's case.
 */
@Entity
public class Tag {
	private String name;

	private boolean searched;

	private Set<Tag> parents;

	private Set<Tag> children;

	private Set<TagMapping> mappings;

	public Tag() {
	}

	@Id
	@Column(length = 50, nullable = false)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(nullable = false)
	public boolean isSearched() {
		return searched;
	}

	public void setSearched(boolean searched) {
		this.searched = searched;
	}

	/*
	 * TODO: many to many isn't supported on App Engine
	 */
	@ManyToMany
	@JoinTable
	public Set<Tag> getParents() {
		return parents;
	}

	public void setParents(Set<Tag> parents) {
		this.parents = parents;
	}

	@ManyToMany(mappedBy = "parents")
	public Set<Tag> getChildren() {
		return children;
	}

	public void setChildren(Set<Tag> children) {
		this.children = children;
	}

	@OneToMany(mappedBy = "tag")
	public Set<TagMapping> getMappings() {
		return mappings;
	}

	public void setMappings(Set<TagMapping> mappings) {
		this.mappings = mappings;
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
		if (!(obj instanceof Tag)) {
			return false;
		}
		Tag other = (Tag) obj;
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
		for (Tag parent : parents) {
			parentString += parent.getName() + ',';
		}
		parentString = parentString.substring(0, parentString.length() - 1);

		return "Tag [name=" + name + ", searched=" + searched + ", parents="
				+ parentString + "]";
	}
}
