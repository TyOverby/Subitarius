/*
 * DomainModule.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.domain;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.Signature;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public final class DomainModule extends AbstractModule {
	@Override
	protected void configure() {
		requestStaticInjection(DistributedEntity.class);
		requestStaticInjection(SignedEntity.class);
	}

	@Provides
	MessageDigest getDigest() {
		try {
			return MessageDigest.getInstance("SHA-256", "BC");
		} catch (GeneralSecurityException gsx) {
			throw new AssertionError(gsx);
		}
	}

	@Provides
	Signature getAlgorithm() {
		try {
			return Signature.getInstance("SHA256withECDSA", "BC");
		} catch (GeneralSecurityException gsx) {
			throw new AssertionError(gsx);
		}
	}
}
