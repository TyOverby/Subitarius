/*
 * ErrorModule.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.instance.client.error;

import com.google.gwt.inject.client.AbstractGinModule;

public class ErrorModule extends AbstractGinModule {
	public ErrorModule() {
	}

	@Override
	protected void configure() {
		bind(ErrorPresenter.Display.class).to(ErrorWidget.class);
		requestStaticInjection(ManagedCallback.class);
	}
}
