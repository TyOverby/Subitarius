/*
 * Team.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.domain;

import static com.google.common.base.Preconditions.*;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.google.common.collect.ImmutableSet;

@Entity
public class Team implements Serializable {
	private Long id;

	private String name;

	private ImmutableSet<User> users;

	private ImmutableSet<License> licenses;

	private transient boolean initialized;

	/**
	 * This constructor should only be invoked by the JPA provider.
	 */
	protected Team() {
		initialized = false;
	}

	public Team(String name) {
		initialized = false;
		setName(name);
		users = ImmutableSet.of();
		licenses = ImmutableSet.of();
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, updatable = false)
	public Long getId() {
		checkState(initialized);
		return id;
	}

	protected void setId(long id) {
		this.id = id;
		initialized = true;
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

	@OneToMany(mappedBy = "team")
	public Set<User> getUsers() {
		return users;
	}

	protected void setUsers(Set<User> users) {
		checkNotNull(users);
		this.users = ImmutableSet.copyOf(users);
	}

	@OneToMany(mappedBy = "team")
	public Set<License> getLicenses() {
		return licenses;
	}

	protected void setLicenses(Set<License> licenses) {
		checkNotNull(licenses);
		this.licenses = ImmutableSet.copyOf(licenses);
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

	private void writeObject(ObjectOutputStream oos) throws IOException {
		checkState(initialized);
		oos.defaultWriteObject();
	}

	private void readObject(ObjectInputStream oos) throws IOException,
			ClassNotFoundException {
		oos.defaultReadObject();
		if (id == null || name == null || users == null || licenses == null) {
			throw new InvalidObjectException("null instance field");
		} else if (name.isEmpty()) {
			throw new InvalidObjectException("empty name");
		}
		initialized = true;
	}
}
