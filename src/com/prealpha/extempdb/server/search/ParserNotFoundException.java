/*
 * ParserNotFoundException.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server.search;

import static com.google.common.base.Preconditions.*;

public class ParserNotFoundException extends Exception {
	private final String domainName;
	
	public ParserNotFoundException(String domainName) {
		super("parser not found for domain name: " + domainName);
		initCause(null);
		
		checkNotNull(domainName);
		checkArgument(!domainName.isEmpty());
		this.domainName = domainName;
	}
	
	public String getDomainName() {
		return domainName;
	}
}
