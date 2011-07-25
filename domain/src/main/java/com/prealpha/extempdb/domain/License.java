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
import java.util.Arrays;
import java.util.Iterator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import com.google.common.collect.Iterators;

@Entity
public class License extends SignedEntity {
	private Team team;

	private byte[] macAddresses;

	/**
	 * This constructor should only be invoked by the JPA provider.
	 */
	protected License() {
	}

	public License(Team team, byte[] macAddresses, PrivateKey privateKey)
			throws InvalidKeyException, SignatureException {
		checkNotNull(team);
		checkNotNull(macAddresses);
		checkNotNull(privateKey);
		checkArgument(macAddresses.length > 0);
		checkArgument(macAddresses.length % 6 == 0);
		this.team = team;
		this.macAddresses = macAddresses;
		sign(privateKey);
	}

	@ManyToOne
	@Column(nullable = false, updatable = false)
	public Team getTeam() {
		return team;
	}

	protected void setTeam(Team team) {
		checkNotNull(team);
		this.team = team;
	}

	@Lob
	@Column(nullable = false, updatable = false)
	protected byte[] getMacAddresses() {
		return macAddresses;
	}

	protected void setMacAddresses(byte[] macAddresses) {
		checkNotNull(macAddresses);
		checkArgument(macAddresses.length > 0);
		checkArgument(macAddresses.length % 6 == 0);
		this.macAddresses = macAddresses;
	}

	public boolean validate() throws IOException {
		Iterator<NetworkInterface> i1 = Iterators
				.forEnumeration(NetworkInterface.getNetworkInterfaces());
		while (i1.hasNext()) {
			NetworkInterface iface = i1.next();
			byte[] mac = iface.getHardwareAddress();

			int macCount = macAddresses.length / 6;
			for (int i = 0; i < macCount; i++) {
				byte[] authorizedMac = Arrays.copyOfRange(macAddresses, 6 * i,
						6 * i + 6);
				if (Arrays.equals(mac, authorizedMac)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	protected byte[] getBytes() {
		byte[] idBytes = getIdBytes();
		byte[] teamBytes = team.getIdBytes();
		return Hashable.merge(idBytes, teamBytes, macAddresses);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(macAddresses);
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
		if (!Arrays.equals(macAddresses, other.macAddresses)) {
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
