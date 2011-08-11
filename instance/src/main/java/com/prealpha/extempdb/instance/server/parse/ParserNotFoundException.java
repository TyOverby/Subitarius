/*
 * ParserNotFoundException.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.server.parse;

import static com.google.common.base.Preconditions.*;

import com.prealpha.extempdb.domain.Source;

public class ParserNotFoundException extends RuntimeException {
	private final String domainName;
	
	private final Source source;
	
	public ParserNotFoundException(String domainName) {
		super("parser not found for domain name: " + domainName);
		checkNotNull(domainName);
		this.domainName = domainName;
		source = null;
	}

	public ParserNotFoundException(Source source) {
		super("parser not found for source: " + source);
		checkNotNull(source);
		domainName = null;
		this.source = source;
	}
	
	public String getDomainName() {
		return domainName;
	}

	public Source getSource() {
		return source;
	}
}
