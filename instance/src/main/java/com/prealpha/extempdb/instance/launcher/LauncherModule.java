/*
 * LauncherModule.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.launcher;

import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.prealpha.extempdb.domain.DistributedEntity;

public final class LauncherModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(UserActionContext.class).in(Singleton.class);
		bind(new TypeLiteral<Iterator<DistributedEntity>>() {
		}).to(EntityIterator.class);
		bind(new TypeLiteral<Iterator<Action>>() {
		}).to(ActionIterator.class);
	}

	@Provides
	ExecutorService getThreadPool() {
		int threads = Runtime.getRuntime().availableProcessors();
		return Executors.newFixedThreadPool(threads);
	}
}
