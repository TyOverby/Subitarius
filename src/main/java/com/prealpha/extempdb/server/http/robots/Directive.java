/*
 * Directive.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server.http.robots;

import static com.google.common.base.Preconditions.*;

import com.google.common.base.Predicate;

abstract class Directive implements Predicate<String> {
	public static enum Type {
		ALLOW(true),

		DISALLOW(true),

		USER_AGENT(true) {
			@Override
			public String toString() {
				return "User-agent";
			}
		};

		private final boolean hasPattern;

		private Type(boolean hasPattern) {
			this.hasPattern = hasPattern;
		}

		public boolean hasPattern() {
			return hasPattern;
		}

		@Override
		public String toString() {
			String name = name().toLowerCase();
			char first = name.charAt(0);
			first = Character.toUpperCase(first);
			return first + name.substring(1);
		}
	}

	private final String field;

	private final Type type;

	protected Directive(String field, Type type) {
		checkNotNull(field);
		checkNotNull(type);
		this.field = field;
		this.type = type;
	}

	protected final String getField() {
		return field;
	}

	public final Type getType() {
		return type;
	}

	@Override
	public String toString() {
		return type + ": " + field + '\n';
	}
}
