/*
 * Tag.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.domain;

import static com.google.common.base.Preconditions.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import com.google.common.collect.ImmutableSet;

/*
 * Note that hashCode() and equals() ignore the tag name's case.
 */
@Entity
public class Tag extends CentralEntity {
	private static final long serialVersionUID = 2124862260409374293L;

	public static enum Type {
		PLACEHOLDER, SEARCHED, ARCHIVED;
	}

	private String name;

	private Type type;

	private ImmutableSet<Tag> parents;

	private transient ImmutableSet<Tag> children;

	private transient ImmutableSet<TagMapping> mappings;

	/**
	 * This constructor should only be invoked by the JPA provider.
	 */
	protected Tag() {
	}

	public Tag(String name, Type type, Set<Tag> parents) {
		setName(name);
		setType(type);
		setParents(parents);
	}

	@Column(length = 50, nullable = false, updatable = false)
	public String getName() {
		return name;
	}

	protected void setName(String name) {
		checkNotNull(name);
		checkArgument(!name.isEmpty());
		this.name = name;
	}

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, updatable = false)
	public Type getType() {
		return type;
	}

	protected void setType(Type type) {
		checkNotNull(type);
		this.type = type;
	}

	@ManyToMany
	@JoinTable(joinColumns = { @JoinColumn(nullable = false, updatable = false) }, inverseJoinColumns = { @JoinColumn(nullable = false, updatable = false) })
	public Set<Tag> getParents() {
		return parents;
	}

	protected void setParents(Set<Tag> parents) {
		checkNotNull(parents);
		checkArgument(!parents.contains(this));
		this.parents = ImmutableSet.copyOf(parents);
	}

	@ManyToMany(mappedBy = "parents")
	public Set<Tag> getChildren() {
		return children;
	}

	protected void setChildren(Set<Tag> children) {
		checkNotNull(children);
		this.children = ImmutableSet.copyOf(children);
	}

	@OneToMany(mappedBy = "tag")
	public Set<TagMapping> getMappings() {
		return mappings;
	}

	protected void setMappings(Set<TagMapping> mappings) {
		checkNotNull(mappings);
		this.mappings = ImmutableSet.copyOf(mappings);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((name == null) ? 0 : name.toUpperCase().hashCode());
		result = prime * result + ((parents == null) ? 0 : parents.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		if (parents == null) {
			if (other.parents != null) {
				return false;
			}
		} else if (!parents.equals(other.parents)) {
			return false;
		}
		if (type != other.type) {
			return false;
		}
		return true;
	}
	
	@Override
	public String toString() {
		return name;
	}

	private void readObject(ObjectInputStream ois) throws IOException,
			ClassNotFoundException {
		ois.defaultReadObject();
		setName(name);
		setType(type);
		setParents(parents);
	}
}
