/*
 * PersistentEntity.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.domain;

import static com.google.common.base.Preconditions.*;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;
import java.util.regex.Pattern;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;

import com.google.common.base.Charsets;
import com.google.common.collect.Sets;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
abstract class PersistentEntity implements Serializable {
	private static final Digest DIGEST = new SHA256Digest();

	private static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();

	private static final Pattern HEX_REGEX = Pattern.compile("[0-9a-f]*");

	private Date createDate;

	private transient Date persistDate;

	private User creator;

	private PersistentEntity parent;

	private transient Set<PersistentEntity> children;

	private transient String storedHash;

	/**
	 * For use by JPA provider only. Subclasses should not invoke this
	 * constructor.
	 */
	protected PersistentEntity() {
		createDate = new Date();
	}

	protected PersistentEntity(User creator) {
		this();
		checkNotNull(creator);
		this.creator = creator;
	}

	protected PersistentEntity(User creator, PersistentEntity parent) {
		this(creator);
		checkNotNull(parent);
		this.parent = parent;
	}

	@Id
	@Column(unique = true, nullable = false)
	public String getHash() {
		byte[] hashBytes = getHashBytes();
		char[] chars = new char[2 * hashBytes.length];
		for (int i = 0; i < hashBytes.length; i++) {
			chars[2 * i] = HEX_CHARS[(hashBytes[i] & 0xF0) >>> 4];
			chars[2 * i + 1] = HEX_CHARS[hashBytes[i] & 0x0F];
		}
		return new String(chars);
	}

	protected final byte[] getHashBytes() {
		String prefix = getClass().getCanonicalName() + '\u0000';
		byte[] prefixBytes = prefix.getBytes(Charsets.UTF_8);
		byte[] payload = toBytes();

		int dataLength = prefixBytes.length + payload.length;
		byte[] data = Arrays.copyOf(prefixBytes, dataLength);
		for (int i = 0; i < payload.length; i++) {
			data[i + prefixBytes.length] = payload[i];
		}

		byte[] hashBytes = new byte[DIGEST.getDigestSize()];
		DIGEST.update(data, 0, data.length);
		DIGEST.doFinal(hashBytes, 0);
		return hashBytes;
	}

	protected void setHash(String hash) {
		checkArgument(hash.length() == 2 * DIGEST.getDigestSize());
		checkArgument(HEX_REGEX.matcher(hash).matches());
		storedHash = hash;
	}

	protected abstract byte[] toBytes();

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	public Date getCreateDate() {
		return createDate;
	}

	protected void setCreateDate(Date createDate) {
		checkNotNull(createDate);
		checkArgument(createDate.compareTo(new Date()) <= 0);
		this.createDate = createDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	public Date getPersistDate() {
		return persistDate;
	}

	protected void setPersistDate(Date persistDate) {
		this.persistDate = persistDate;
	}

	@PrePersist
	@SuppressWarnings("unused")
	private void onPersist() {
		persistDate = new Date();
	}

	@ManyToOne
	@Column(nullable = false)
	public User getCreator() {
		return creator;
	}

	protected void setCreator(User creator) {
		checkNotNull(creator);
		this.creator = creator;
	}

	@ManyToOne
	@Column(nullable = true)
	public PersistentEntity getParent() {
		return parent;
	}

	protected void setParent(PersistentEntity parent) {
		this.parent = parent;
	}

	@OneToMany(mappedBy = "parent")
	public Set<PersistentEntity> getChildren() {
		return children;
	}

	protected void setChildren(Set<PersistentEntity> children) {
		checkNotNull(children);
		this.children = children;
	}

	/*
	 * This can't be done in the setter methods, because JPA doesn't guarantee
	 * the order of setter invocation.
	 */
	@PostLoad
	@SuppressWarnings("unused")
	private void validate() {
		if (persistDate != null) {
			checkState(createDate.compareTo(persistDate) <= 0);
		}
		if (storedHash != null) {
			checkState(getHash().equals(storedHash));
			storedHash = null;
		}
	}

	private void writeObject(ObjectOutputStream oos) throws IOException {
		oos.defaultWriteObject();
		oos.writeInt(children.size());
		for (PersistentEntity child : children) {
			oos.writeObject(child);
		}
	}

	private void readObject(ObjectInputStream ois) throws IOException,
			ClassNotFoundException {
		ois.defaultReadObject();
		int childCount = ois.readInt();
		children = Sets.newHashSetWithExpectedSize(childCount);
		for (int i = 0; i < childCount; i++) {
			children.add((PersistentEntity) ois.readObject());
		}

		if (createDate == null) {
			throw new InvalidObjectException("null create date");
		} else if (creator == null) {
			throw new InvalidObjectException("null creator");
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
