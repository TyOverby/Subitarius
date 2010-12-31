/*
 * Slf4jMembersInjector.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server;

import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.MembersInjector;

class Slf4jMembersInjector<T> implements MembersInjector<T> {
	private final Field field;

	private final Logger log;

	Slf4jMembersInjector(Field field) {
		this.field = field;
		this.log = LoggerFactory.getLogger(field.getDeclaringClass());
		field.setAccessible(true);
	}

	@Override
	public void injectMembers(T t) {
		try {
			field.set(t, log);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}
