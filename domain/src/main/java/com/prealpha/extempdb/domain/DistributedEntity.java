/*
 * DistributedEntity.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.domain;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

/**
 * Abstract base class for entities which are "distributed" across multiple
 * client instances. Because they are immutable, changes cannot be made to these
 * entities directly. Instead, a completely new child object with the required
 * changes is created. The parent and child attributes store the relationship
 * between the old (overridden) and new entities. This scheme provides a sort of
 * version tracking when objects are shared by multiple clients.
 * 
 * @author Meyer Kizner
 * 
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class DistributedEntity extends ImmutableEntity {
	private User creator;

	private DistributedEntity parent;

	private transient DistributedEntity child;

	protected DistributedEntity() {
		this(null, null);
	}

	protected DistributedEntity(User creator) {
		this(creator, null);
	}

	protected DistributedEntity(DistributedEntity parent) {
		this(null, parent);
	}

	protected DistributedEntity(User creator, DistributedEntity parent) {
		this.creator = creator;
		this.parent = parent;
	}

	@ManyToOne
	@JoinColumn(updatable = false)
	public User getCreator() {
		return creator;
	}

	protected void setCreator(User creator) {
		this.creator = creator;
	}

	@OneToOne
	@JoinColumn(updatable = false)
	public DistributedEntity getParent() {
		return parent;
	}

	protected void setParent(DistributedEntity parent) {
		this.parent = parent;
	}

	@OneToOne(mappedBy = "parent")
	public DistributedEntity getChild() {
		return child;
	}

	protected void setChild(DistributedEntity child) {
		this.child = child;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * 
	 * {@code DistributedEntity} adds the additional requirement that the result
	 * of this method must be consistent with {@link Object#equals(Object)
	 * equals}. For a particular class, equal objects must generate equal byte
	 * representations, and unequal objects must generate unequal byte
	 * representations. (This is stronger than the requirement for
	 * {@link Object#hashCode() hashCode}.)
	 */
	@Override
	protected abstract byte[] getBytes();
}
