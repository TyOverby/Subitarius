/*
 * DeletedEntity.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.domain;

import javax.persistence.Entity;
import javax.persistence.Transient;

@Entity
public class DeletedEntity extends DistributedEntity {
	private static final long serialVersionUID = 2380244029703081358L;

	/**
	 * This constructor should only be invoked by the JPA provider.
	 */
	protected DeletedEntity() {
	}

	public DeletedEntity(Team creator, DistributedEntity parent) {
		super(creator, parent);
	}

	@Transient
	@Override
	public byte[] getBytes() {
		return getParent().getBytes();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((getParent() == null) ? 0 : getParent().hashCode());
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
		if (!(obj instanceof DeletedEntity)) {
			return false;
		}
		DeletedEntity other = (DeletedEntity) obj;
		if (getParent() == null) {
			if (other.getParent() != null) {
				return false;
			}
		} else if (!getParent().equals(other.getParent())) {
			return false;
		}
		return true;
	}
}
