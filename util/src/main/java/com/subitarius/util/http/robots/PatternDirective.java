/*
 * PatternDirective.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.util.http.robots;

import static com.google.common.base.Preconditions.*;

class PatternDirective extends Directive {
	public PatternDirective(String pattern, Type type) {
		super(pattern, type);
		checkArgument(type.hasPattern());
	}

	@Override
	public boolean apply(String value) {
		checkNotNull(value);
		final String PATTERN = getField();

		if (PATTERN.isEmpty()) {
			return false;
		}

		String regex = quote(PATTERN).replace("\\*", ".*");
		if (!PATTERN.endsWith("$")) {
			regex += ".*";
		} else {
			// the $ will have been escaped as \$ - we don't want it
			regex = regex.substring(0, regex.length() - 2);
		}

		return value.matches(regex);
	}

	private static String quote(String pattern) {
		return pattern.replace("\\", "\\\\").replace("$", "\\$")
				.replace("^", "\\^").replace(".", "\\.").replace("*", "\\*")
				.replace("+", "\\+").replace("?", "\\?").replace("[", "\\[")
				.replace("]", "\\]");
	}
}
