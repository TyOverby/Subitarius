/*
 * License.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.domain;

import static com.google.common.base.Preconditions.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.NetworkInterface;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.SignatureException;
import java.util.Iterator;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;
import com.google.common.primitives.Longs;

@Entity
public class License extends SignedEntity {
	private Team team;

	private ImmutableSet<Long> macAddresses;

	/**
	 * This constructor should only be invoked by the JPA provider.
	 */
	protected License() {
	}

	public License(Team team, Set<Long> macAddresses, PrivateKey privateKey)
			throws InvalidKeyException, SignatureException {
		checkNotNull(privateKey);
		setTeam(team);
		setMacAddresses(macAddresses);
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

	@ElementCollection
	@Column(nullable = false, updatable = false)
	protected Set<Long> getMacAddresses() {
		return macAddresses;
	}

	protected void setMacAddresses(Set<Long> macAddresses) {
		checkNotNull(macAddresses);
		for (long mac : macAddresses) {
			// valid range of six-byte MAC address
			checkArgument(mac >= 0);
			checkArgument(mac < 0x0001000000000000L);
		}
		this.macAddresses = ImmutableSet.copyOf(macAddresses);
	}

	public boolean validate() throws IOException {
		Iterator<NetworkInterface> i1 = Iterators
				.forEnumeration(NetworkInterface.getNetworkInterfaces());
		while (i1.hasNext()) {
			NetworkInterface iface = i1.next();
			byte[] macBytes = new byte[8];
			System.arraycopy(iface.getHardwareAddress(), 0, macBytes, 2, 6);
			long mac = Longs.fromByteArray(macBytes);
			if (macAddresses.contains(mac)) {
				return true;
			}
		}
		return false;
	}

	@Transient
	@Override
	protected byte[] getBytes() {
		byte[] idBytes = getIdBytes();
		byte[] teamBytes = team.getIdBytes();
		// XOR all MACs together to prevent the order from affecting the hash
		long macBytes = 0;
		for (long mac : macAddresses) {
			macBytes ^= mac;
		}
		return Hashable.merge(idBytes, teamBytes, Longs.toByteArray(macBytes));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((macAddresses == null) ? 0 : macAddresses.hashCode());
		result = prime * result + ((team == null) ? 0 : team.hashCode());
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
		if (!(obj instanceof License)) {
			return false;
		}
		License other = (License) obj;
		if (macAddresses == null) {
			if (other.macAddresses != null) {
				return false;
			}
		} else if (!macAddresses.equals(other.macAddresses)) {
			return false;
		}
		if (team == null) {
			if (other.team != null) {
				return false;
			}
		} else if (!team.equals(other.team)) {
			return false;
		}
		return true;
	}

	private void readObject(ObjectInputStream ois) throws IOException,
			ClassNotFoundException {
		ois.defaultReadObject();
		setTeam(team);
		setMacAddresses(macAddresses);
	}
}
