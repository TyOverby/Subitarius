/*
 * CoreModule.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.client.core;

import com.google.gwt.inject.client.AbstractGinModule;

public class CoreModule extends AbstractGinModule {
	public CoreModule() {
	}

	@Override
	protected void configure() {
		bind(LoginStatusPresenter.Display.class).to(LoginStatusWidget.class);
	}
}
