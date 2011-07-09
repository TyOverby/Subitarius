/*
 * DomainModule.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.domain;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.google.inject.AbstractModule;

public final class DomainModule extends AbstractModule {
	public DomainModule() {
	}

	@Override
	protected void configure() {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			bind(MessageDigest.class).toInstance(digest);
			requestStaticInjection(PersistentEntity.class);
		} catch (NoSuchAlgorithmException nsax) {
			// SHA-256 is required by the crypto spec
			throw new AssertionError(nsax);
		}
	}
}
