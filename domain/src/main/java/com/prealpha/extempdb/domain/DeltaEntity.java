/*
 * DeltaEntity.java
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
 * Abstract base class for entities which participate in object deltas. Because
 * they are immutable, changes cannot be made to these entities directly.
 * Instead, a child entity which overrides the parent should be created. When
 * merging an object delta into the local revision, situations may arise where
 * two entities have the same parent; in this case, the user will be prompted to
 * resolve the conflict.
 * 
 * @author Meyer Kizner
 * 
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class DeltaEntity extends ImmutableEntity {
	private User creator;

	private DeltaEntity parent;

	private DeltaEntity child;

	protected DeltaEntity() {
		this(null, null);
	}

	protected DeltaEntity(User creator) {
		this(creator, null);
	}
	
	protected DeltaEntity(DeltaEntity parent) {
		this(null, parent);
	}

	protected DeltaEntity(User creator, DeltaEntity parent) {
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
	public DeltaEntity getParent() {
		return parent;
	}

	protected void setParent(DeltaEntity parent) {
		this.parent = parent;
	}

	@OneToOne(mappedBy = "parent")
	public DeltaEntity getChild() {
		return child;
	}

	protected void setChild(DeltaEntity child) {
		this.child = child;
	}
}
