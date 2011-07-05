/*
 * ParserNotFoundException.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server.parse;

import static com.google.common.base.Preconditions.*;

import com.prealpha.extempdb.server.domain.Source;

public class ParserNotFoundException extends RuntimeException {
	private final Source source;

	public ParserNotFoundException(Source source) {
		super("parser not found for source: " + source);
		checkNotNull(source);
		this.source = source;
	}

	public Source getSource() {
		return source;
	}
}
