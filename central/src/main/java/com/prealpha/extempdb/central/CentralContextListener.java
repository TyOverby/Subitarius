/*
 * CentralContextListener.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.central;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.google.inject.servlet.GuiceServletContextListener;

public final class CentralContextListener extends GuiceServletContextListener {
	public CentralContextListener() {
	}

	@Override
	protected Injector getInjector() {
		return Guice.createInjector(new JpaPersistModule("central"));
	}
}
