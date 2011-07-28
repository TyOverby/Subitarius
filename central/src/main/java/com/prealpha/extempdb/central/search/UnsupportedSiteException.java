/*
 * UnsupportedSiteException.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.central.search;

import static com.google.common.base.Preconditions.*;

public class UnsupportedSiteException extends Exception {
	private final String domainName;
	
	public UnsupportedSiteException(String domainName) {
		super("no parser support for site: " + domainName);
		checkNotNull(domainName);
		checkArgument(!domainName.isEmpty());
		this.domainName = domainName;
	}
	
	public String getDomainName() {
		return domainName;
	}
}
