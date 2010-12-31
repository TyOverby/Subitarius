/*
 * ErrorModule.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.client.error;

import com.google.gwt.inject.client.AbstractGinModule;

public class ErrorModule extends AbstractGinModule {
	@Override
	protected void configure() {
		bind(ErrorPresenter.Display.class).to(ErrorWidget.class);
	}
}
