/*
 * DomainModule.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.domain;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.Security;
import java.security.Signature;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public final class DomainModule extends AbstractModule {
	@Override
	protected void configure() {
		Security.addProvider(new BouncyCastleProvider());
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
			return Signature.getInstance("SHA512withECDSA", "BC");
		} catch (GeneralSecurityException gsx) {
			throw new AssertionError(gsx);
		}
	}
}
