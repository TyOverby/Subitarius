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

	/**
	 * @return the length, in bytes, of hashes produced by
	 *         {@link #getHashBytes()}
	 */
	static int getHashLength() {
		return digest.getDigestLength();
	}

	/**
	 * Merges a number of byte arrays into a single array. This utility method
	 * is helpful in implementing {@link #getBytes()} in subclasses.
	 * 
	 * @param arrays
	 *            an array of byte arrays
	 * @return a single byte array representing the entire input concatenated
	 *         together, with null bytes between each entry in the input
	 */
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

	/**
	 * Merges an {@link Iterable} of byte arrays into a single array. This
	 * utility method is helpful in implementing {@link #getBytes()} in
	 * subclasses.
	 * 
	 * @param arrays
	 *            an {@code Iterable} of byte arrays
	 * @return a single byte array representing the entire input concatenated
	 *         together, with null bytes between each entry in the input
	 */
	static byte[] merge(Iterable<byte[]> arrays) {
		int totalLength = 0;
		for (byte[] array : arrays) {
			totalLength += array.length + 1;
		}
		byte[] merged = new byte[totalLength];
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
		byte[] payload = getBytes();
		byte[] data = merge(prefixBytes, payload);
		return digest.digest(data);
	}

	/**
	 * Generates a representation of this object as a {@code byte} array,
	 * suitable for calculation of a hash. This representation is not intended
	 * for use in restoring the original state of the object. Therefore, the
	 * meanings of the bytes within the output are implementation details and
	 * are specific to the implementing class; they should not be documented.
	 * 
	 * @return a {@code byte} array representation of this object
	 */
	protected abstract byte[] getBytes();
}
