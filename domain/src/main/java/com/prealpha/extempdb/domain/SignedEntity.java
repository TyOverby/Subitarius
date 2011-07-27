/*
 * SignedEntity.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.domain;

import static com.google.common.base.Preconditions.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import com.google.common.primitives.Longs;
import com.google.inject.Inject;

@MappedSuperclass
abstract class SignedEntity implements HasBytes, Serializable {
	private static final long serialVersionUID = -5961578748349316744L;

	private static final Pattern UUID_REGEX = Pattern
			.compile("[0-9a-f]{8}-([0-9a-f]{4}-){3}[0-9a-f]{12}");

	@Inject
	private static Signature algorithm;

	private UUID uuid;

	private byte[] signature;

	protected SignedEntity() {
		uuid = UUID.randomUUID();
	}

	@Id
	@Column(length = 36, nullable = false, updatable = false)
	protected String getId() {
		return uuid.toString();
	}

	@Transient
	protected byte[] getIdBytes() {
		byte[] idBytes = new byte[16];
		byte[] upperBytes = Longs.toByteArray(uuid.getMostSignificantBits());
		byte[] lowerBytes = Longs.toByteArray(uuid.getLeastSignificantBits());
		System.arraycopy(upperBytes, 0, idBytes, 0, 8);
		System.arraycopy(lowerBytes, 0, idBytes, 8, 8);
		return idBytes;
	}

	protected void setId(String id) {
		checkNotNull(id);
		checkArgument(UUID_REGEX.matcher(id).matches());
		uuid = UUID.fromString(id);
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
		checkNotNull(uuid);
		if (signature != null) {
			setSignature(signature);
		}
	}
}
