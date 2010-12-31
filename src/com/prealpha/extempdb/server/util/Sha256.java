/*
 * Sha256.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server.util;

import java.security.MessageDigest;

public final class Sha256 {
	public static String hashAsHex(String text) {
		byte[] hash = hash(text.getBytes());
		String result = "";
		for (int i = 0; i < hash.length; i++) {
			result += Integer.toString((hash[i] & 0xff) + 0x100, 16).substring(
					1);
		}
		return result;
	}

	public static String hashAsBase64(String text) {
		byte[] hash = hash(text.getBytes());
		return new String(Base64Coder.encode(hash));
	}

	private static byte[] hash(byte[] input) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(input, 0, input.length);
			return md.digest();
		} catch (Exception x) {
			throw new IllegalStateException(x);
		}
	}

	private Sha256() {
	}
}
