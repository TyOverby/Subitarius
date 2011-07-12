/*
 * License.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.domain;

import static com.google.common.base.Preconditions.*;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Lob;
import javax.persistence.OneToOne;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

public class License extends PersistentEntity {
	private long id;

	private GlobalUser owner;

	private Set<List<Byte>> macAddresses;

	private BigInteger r;

	private BigInteger s;

	protected License() {
	}

	@Column(unique = true, nullable = false)
	protected long getId() {
		return id;
	}

	protected void setId(long id) {
		this.id = id;
	}

	@OneToOne
	@Column(nullable = false)
	protected GlobalUser getOwner() {
		return owner;
	}

	protected void setOwner(GlobalUser owner) {
		this.owner = owner;
	}

	@Lob
	@Column(nullable = false)
	protected byte[] getMacAddresses() {
		byte[] bytes = new byte[6 * macAddresses.size()];
		int i = 0;
		for (List<Byte> mac : macAddresses) {
			for (byte b : mac) {
				bytes[i++] = b;
			}
		}
		return bytes;
	}

	protected void setMacAddresses(byte[] macAddresses) {
		checkArgument(macAddresses.length % 6 == 0);
		int count = macAddresses.length / 6;
		this.macAddresses = Sets.newHashSetWithExpectedSize(count);
		for (int i = 0; i < count; i++) {
			int begin = i * 6;
			int end = begin + 6;
			byte[] macBytes = Arrays.copyOfRange(macAddresses, begin, end);
			List<Byte> mac = ImmutableList.of(macBytes[0], macBytes[1],
					macBytes[2], macBytes[3], macBytes[4], macBytes[5]);
			this.macAddresses.add(mac);
		}
	}

	@Column(nullable = false)
	protected BigInteger getR() {
		return r;
	}

	protected void setR(BigInteger r) {
		this.r = r;
	}

	@Column(nullable = false)
	protected BigInteger getS() {
		return s;
	}

	protected void setS(BigInteger s) {
		this.s = s;
	}

	/*
	 * XOR is used for the MAC addresses so that the resulting hash is not
	 * dependent on the address order. This does decrease the entropy of the
	 * hash, however, but there shouldn't be duplicate IDs anyway.
	 */
	@Override
	protected byte[] toBytes() {
		byte[] macBytes = new byte[6];
		for (List<Byte> mac : macAddresses) {
			for (int i = 0; i < 6; i++) {
				macBytes[i] ^= mac.get(i);
			}
		}

		ByteBuffer bytes = ByteBuffer.allocate(46);
		bytes.putLong(id);
		bytes.put(owner.getHashBytes());
		bytes.put(macBytes);
		return bytes.array();
	}
}
