/*
 * SignedEntity.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.domain;

import static com.google.common.base.Preconditions.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;

import javax.persistence.Column;
import javax.persistence.Lob;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import com.google.inject.Inject;

@MappedSuperclass
abstract class SignedEntity extends CentralEntity implements HasBytes {
	private static final long serialVersionUID = 8109262713725278109L;

	@Inject
	private static Signature algorithm;

	private byte[] signature;

	protected SignedEntity() {
	}

	@Lob
	@Column(nullable = false)
	protected byte[] getSignature() {
		checkState(signature != null);
		return signature;
	}

	protected void setSignature(byte[] signature) {
		checkNotNull(signature);
		this.signature = signature;
	}

	@Transient
	public boolean isSigned() {
		return (signature != null);
	}

	protected void sign(PrivateKey privateKey) throws InvalidKeyException,
			SignatureException {
		algorithm.initSign(privateKey);
		algorithm.update(getBytes());
		signature = algorithm.sign();
	}

	public boolean verify(PublicKey publicKey) throws InvalidKeyException,
			SignatureException {
		checkState(signature != null);
		algorithm.initVerify(publicKey);
		algorithm.update(getBytes());
		return algorithm.verify(signature);
	}

	private void readObject(ObjectInputStream ois) throws IOException,
			ClassNotFoundException {
		ois.defaultReadObject();
		if (signature != null) {
			setSignature(signature);
		}
	}
}
