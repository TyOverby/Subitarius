/*
 * Hashable.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.domain;

import java.security.MessageDigest;
import java.util.Arrays;

import com.google.common.base.Charsets;
import com.google.inject.Inject;

abstract class Hashable {
	@Inject
	private static MessageDigest digest;

	protected Hashable() {
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
		return digest.digest(data);
	}

	protected abstract byte[] toBytes();
}
