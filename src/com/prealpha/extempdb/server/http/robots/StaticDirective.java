/*
 * StaticDirective.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server.http.robots;

class StaticDirective extends Directive {
	public StaticDirective(String field, Type type) {
		super(field, type);
	}

	@Override
	public boolean apply(String value) {
		assert false;
		throw new UnsupportedOperationException();
	}
}
