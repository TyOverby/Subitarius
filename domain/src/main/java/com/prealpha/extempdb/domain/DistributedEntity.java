/*
 * DistributedEntity.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.domain;

import static com.google.common.base.Preconditions.*;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Date;
import java.util.regex.Pattern;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Abstract base class for entities which are "distributed" across multiple
 * client instances. All such entities are immutable and cannot be changed
 * directly. To modify a distributed entity, a completely new child object with
 * the required changes is created. The parent and child attributes store the
 * relationship between the old (overridden) and new entities. This scheme
 * provides a sort of version tracking when objects are shared by multiple
 * clients.
 * <p>
 * 
 * One of the design goals for this class is to provide a consistent object
 * identifier for use across multiple clients, without any central issuing
 * authority. This goal is achieved by (indirectly) using the result of
 * {@link #getHashBytes()} as the identifier for entities inheriting from this
 * class. As a result, all concrete subclasses must implement
 * {@link #getBytes()}, which has a stronger contract in this class than is
 * inherited from {@link Hashable}. See that method for details.
 * 
 * @author Meyer Kizner
 * @see #getBytes()
 * 
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class DistributedEntity extends Hashable implements
		Serializable {
	private static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();

	private static final Pattern HEX_REGEX = Pattern.compile("[0-9a-f]*");

	private Date createDate;

	private transient Date persistDate;

	private User creator;

	private DistributedEntity parent;

	private transient DistributedEntity child;

	private transient String storedHash;

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
		createDate = new Date();
		this.creator = creator;
		this.parent = parent;
	}

	@Id
	@Column(nullable = false, updatable = false)
	protected String getHash() {
		byte[] hashBytes = getHashBytes();
		char[] chars = new char[2 * hashBytes.length];
		for (int i = 0; i < hashBytes.length; i++) {
			chars[2 * i] = HEX_CHARS[(hashBytes[i] & 0xF0) >>> 4];
			chars[2 * i + 1] = HEX_CHARS[hashBytes[i] & 0x0F];
		}
		return new String(chars);
	}

	protected void setHash(String hash) {
		checkArgument(HEX_REGEX.matcher(hash).matches());
		storedHash = hash;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false, updatable = false)
	public Date getCreateDate() {
		return new Date(createDate.getTime());
	}

	protected void setCreateDate(Date createDate) {
		checkNotNull(createDate);
		checkArgument(createDate.compareTo(new Date()) <= 0);
		this.createDate = new Date(createDate.getTime());
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false, updatable = false)
	public Date getPersistDate() {
		return new Date(persistDate.getTime());
	}

	protected void setPersistDate(Date persistDate) {
		if (persistDate != null) {
			checkArgument(persistDate.compareTo(new Date()) <= 0);
			this.persistDate = new Date(persistDate.getTime());
		} else {
			this.persistDate = null;
		}
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

	@OneToOne(mappedBy = "child")
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
	 * <p>
	 * 
	 * In general, subclass implementations should include only the state of
	 * that subclass in the result of this method. The identifier column for
	 * {@code DistributedEntity} becomes much less useful if properties such as
	 * the creator or creation date are used to determine the hash.
	 */
	@Override
	protected abstract byte[] getBytes();

	@PrePersist
	@SuppressWarnings("unused")
	private void onPersist() {
		persistDate = new Date();
	}

	/*
	 * This can't be done in the setter methods, because JPA doesn't guarantee
	 * the order of setter invocation.
	 */
	@PostLoad
	private void validate() {
		if (persistDate != null) {
			checkState(createDate.compareTo(persistDate) <= 0);
		}
		if (storedHash != null) {
			checkState(getHash().equals(storedHash));
			storedHash = null;
		}
	}

	private void readObject(ObjectInputStream ois) throws IOException,
			ClassNotFoundException {
		ois.defaultReadObject();
		setCreateDate(createDate);
		setPersistDate(persistDate);
		validate();
	}

	/*
	 * See Effective Java, second edition, item 74.
	 */
	@SuppressWarnings("unused")
	private void readObjectNoData() throws ObjectStreamException {
		throw new InvalidObjectException("stream data required");
	}
}
