/*
 * AbstractSourceParser.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server.parse;

abstract class AbstractSourceParser implements SourceParser {
	@Override
	public String getCanonicalUrl(String url) {
		int parameterIndex = url.indexOf('?');

		if (parameterIndex >= 0) {
			return url.substring(0, parameterIndex);
		} else {
			return url;
		}
	}
}
