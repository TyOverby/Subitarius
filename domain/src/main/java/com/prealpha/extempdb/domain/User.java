/*
 * User.java
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
import java.util.regex.Pattern;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import com.google.common.base.Charsets;

/*
 * Note that hashCode() and equals() ignore the user name's case.
 */
@Entity
public class User extends SignedEntity {
	private static final long serialVersionUID = 2141076023094921025L;

	private static final int BCRYPT_ROUNDS = 12;

	private static final Pattern BCRYPT_REGEX = Pattern.compile("\\$2a\\$"
			+ BCRYPT_ROUNDS + "\\$[./A-Za-z0-9]{53}");

	private String name;

	private String hash;

	private Team team;

	/**
	 * This constructor should only be invoked by the JPA provider.
	 */
	protected User() {
	}

	public User(String name, String password, Team team, PrivateKey privateKey)
			throws InvalidKeyException, SignatureException {
		checkNotNull(name);
		checkNotNull(password);
		checkNotNull(team);
		checkNotNull(privateKey);
		checkArgument(!name.isEmpty());
		this.name = name;
		this.team = team;
		setPassword(password, privateKey);
	}

	@Column(unique = true, nullable = false, updatable = false)
	public String getName() {
		return name;
	}

	protected void setName(String name) {
		checkNotNull(name);
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

	@ManyToOne
	@JoinColumn(nullable = false, updatable = false)
	public Team getTeam() {
		return team;
	}

	protected void setTeam(Team team) {
		checkNotNull(team);
		this.team = team;
	}

	@Transient
	@Override
	public byte[] getBytes() {
		byte[] idBytes = getIdBytes();
		byte[] nameBytes = name.getBytes(Charsets.UTF_8);
		byte[] hashBytes = hash.getBytes();
		byte[] teamBytes = team.getIdBytes();
		return DistributedEntity
				.merge(idBytes, nameBytes, hashBytes, teamBytes);
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
		if (!(obj instanceof User)) {
			return false;
		}
		User other = (User) obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equalsIgnoreCase(other.name)) {
			return false;
		}
		return true;
	}

	private void readObject(ObjectInputStream ois) throws IOException,
			ClassNotFoundException {
		ois.defaultReadObject();
		setName(name);
		setHash(hash);
		setTeam(team);
	}
}
