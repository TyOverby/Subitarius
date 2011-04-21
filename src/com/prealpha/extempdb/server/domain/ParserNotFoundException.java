/*
 * ParserNotFoundException.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server.domain;

import static com.google.common.base.Preconditions.*;

public class ParserNotFoundException extends Exception {
	private final String domainName;

	public ParserNotFoundException(String domainName) {
		this(domainName, null);
	}

	public ParserNotFoundException(String domainName, Throwable cause) {
		super("parser not found for domain name: " + domainName, cause);
		checkNotNull(domainName);
		checkArgument(!domainName.isEmpty());
		this.domainName = domainName;
	}

	public String getDomainName() {
		return domainName;
	}
}
