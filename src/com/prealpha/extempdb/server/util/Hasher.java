/*
 * Hasher.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server.util;

import java.security.MessageDigest;

import com.google.inject.Inject;
import com.google.inject.Provider;

public final class Hasher {
	private final Provider<MessageDigest> digestProvider;

	@Inject
	public Hasher(Provider<MessageDigest> digestProvider) {
		this.digestProvider = digestProvider;
	}

	public byte[] hash(byte[] data) {
		MessageDigest digest = digestProvider.get();
		digest.update(data, 0, data.length);
		return digest.digest();
	}
}
