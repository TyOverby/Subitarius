/*
 * Hashable.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.domain;

import java.security.MessageDigest;

import com.google.common.base.Charsets;
import com.google.inject.Inject;

abstract class Hashable {
	@Inject
	private static MessageDigest digest;

	static byte[] merge(byte[]... arrays) {
		int totalLength = 0;
		for (byte[] array : arrays) {
			totalLength += array.length;
		}
		byte[] merged = new byte[totalLength + arrays.length];
		int pos = 0;
		for (byte[] array : arrays) {
			System.arraycopy(array, 0, merged, pos, array.length);
			merged[pos + array.length] = 0x00;
			pos += array.length + 1;
		}
		return merged;
	}

	protected Hashable() {
	}

	protected final byte[] getHashBytes() {
		String prefix = getClass().getCanonicalName();
		byte[] prefixBytes = prefix.getBytes(Charsets.UTF_8);
		byte[] payload = toBytes();
		byte[] data = merge(prefixBytes, payload);
		return digest.digest(data);
	}

	protected abstract byte[] toBytes();
}
