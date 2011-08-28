/*
 * StaticDirective.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.util.http.robots;

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
