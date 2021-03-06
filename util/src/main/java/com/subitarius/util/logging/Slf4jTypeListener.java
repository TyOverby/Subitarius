/*
 * Slf4jTypeListener.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.util.logging;

import java.lang.reflect.Field;

import org.slf4j.Logger;

import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

public class Slf4jTypeListener implements TypeListener {
	public Slf4jTypeListener() {
	}

	@Override
	public <T> void hear(TypeLiteral<T> typeLiteral,
			TypeEncounter<T> typeEncounter) {
		for (Field field : typeLiteral.getRawType().getDeclaredFields()) {
			if (field.getType() == Logger.class
					&& field.isAnnotationPresent(InjectLogger.class)) {
				typeEncounter.register(new Slf4jMembersInjector<T>(field));
			}
		}
	}
}
