/*
 * Team.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.domain;

import static com.google.common.base.Preconditions.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.SignatureException;
import java.util.Date;
import java.util.Set;
import java.util.regex.Pattern;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableSet;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;

@Entity
public class Team extends SignedEntity {
	private static final long serialVersionUID = -5847806816422177475L;

	private static final int BCRYPT_ROUNDS = 12;

	private static final Pattern BCRYPT_REGEX = Pattern.compile("\\$2a\\$"
			+ BCRYPT_ROUNDS + "\\$[./A-Za-z0-9]{53}");

	private String name;

	private String hash;

	private Date expiry;

	private int licenseCap;

	private transient ImmutableSet<License> licenses;

	/**
	 * This constructor should only be invoked by the JPA provider.
	 */
	protected Team() {
	}

	public Team(String name, String password, Date expiry, int licenseCap,
			PrivateKey privateKey) throws InvalidKeyException,
			SignatureException {
		setName(name);
		setExpiry(expiry);
		setLicenseCap(licenseCap);
		setPassword(password, privateKey); // calls sign(PrivateKey)
		licenses = ImmutableSet.of();
	}

	@Column(unique = true, nullable = false, updatable = false)
	public String getName() {
		return name;
	}

	protected void setName(String name) {
		checkNotNull(name);
		checkArgument(!name.isEmpty());
		this.name = name;
	}

	@Column(length = 60, nullable = false)
	protected String getHash() {
		return hash;
	}

	protected void setHash(String hash) {
		checkNotNull(hash);
		checkArgument(BCRYPT_REGEX.matcher(hash).matches());
		this.hash = hash;
	}

	public boolean authenticate(String password) {
		return BCrypt.checkpw(password, hash);
	}

	public void setPassword(String password, PrivateKey privateKey)
			throws InvalidKeyException, SignatureException {
		String salt = BCrypt.gensalt(BCRYPT_ROUNDS);
		hash = BCrypt.hashpw(password, salt);
		sign(privateKey);
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	protected Date getExpiry() {
		return new Date(expiry.getTime());
	}

	protected void setExpiry(Date expiry) {
		checkNotNull(expiry);
		this.expiry = new Date(expiry.getTime());
	}

	@Transient
	public boolean isExpired() {
		return (expiry.compareTo(new Date()) < 0);
	}

	public void updateExpiry(Date newExpiry, PrivateKey privateKey)
			throws InvalidKeyException, SignatureException {
		checkNotNull(newExpiry);
		checkArgument(newExpiry.compareTo(expiry) > 0);
		setExpiry(newExpiry);
		sign(privateKey);
	}

	@Column(nullable = false)
	public int getLicenseCap() {
		return licenseCap;
	}

	protected void setLicenseCap(int licenseCap) {
		checkArgument(licenseCap > 0);
		this.licenseCap = licenseCap;
	}

	@OneToMany(mappedBy = "team")
	public Set<License> getLicenses() {
		return licenses;
	}

	protected void setLicenses(Set<License> licenses) {
		checkNotNull(licenses);
		this.licenses = ImmutableSet.copyOf(licenses);
	}

	@Transient
	@Override
	public byte[] getBytes() {
		byte[] idBytes = getIdBytes();
		byte[] nameBytes = name.getBytes(Charsets.UTF_8);
		byte[] expiryBytes = Longs.toByteArray(expiry.getTime());
		byte[] licenseCapBytes = Ints.toByteArray(licenseCap);
		return DistributedEntity.merge(idBytes, nameBytes, expiryBytes,
				licenseCapBytes);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		if (!(obj instanceof Team)) {
			return false;
		}
		Team other = (Team) obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
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
		setExpiry(expiry);
		setLicenseCap(licenseCap);
	}
}
