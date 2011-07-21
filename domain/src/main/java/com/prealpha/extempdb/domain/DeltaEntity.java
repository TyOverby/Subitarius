/*
 * DeltaEntity.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.domain;

import static com.google.common.base.Preconditions.*;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.google.common.collect.ImmutableSet;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class DeltaEntity extends ImmutableEntity {
	private User creator;

	private ImmutableEntity parent;

	private ImmutableSet<ImmutableEntity> children;

	/**
	 * This constructor should only be invoked by the JPA provider.
	 */
	protected DeltaEntity() {
	}
	
	protected DeltaEntity(User creator) {
		checkNotNull(creator);
		this.creator = creator;
	}
	
	protected DeltaEntity(User creator, ImmutableEntity parent) {
		this(creator);
		checkNotNull(parent);
		this.parent = parent;
	}

	@ManyToOne
	@JoinColumn(nullable = false, updatable = false)
	public User getCreator() {
		return creator;
	}

	protected void setCreator(User creator) {
		checkNotNull(creator);
		this.creator = creator;
	}

	@ManyToOne
	@JoinColumn(updatable = false)
	public ImmutableEntity getParent() {
		return parent;
	}

	protected void setParent(ImmutableEntity parent) {
		this.parent = parent;
	}

	@OneToMany(mappedBy = "parent")
	public Set<ImmutableEntity> getChildren() {
		return children;
	}

	protected void setChildren(Set<ImmutableEntity> children) {
		checkNotNull(children);
		this.children = ImmutableSet.copyOf(children);
	}

	private void readObject(ObjectInputStream ois) throws IOException,
			ClassNotFoundException {
		if (creator == null || children == null) {
			throw new InvalidObjectException("null instance field");
		}
	}

	/*
	 * See Effective Java, second edition, item 74.
	 */
	@SuppressWarnings("unused")
	private void readObjectNoData() throws ObjectStreamException {
		throw new InvalidObjectException("stream data required");
	}
}
