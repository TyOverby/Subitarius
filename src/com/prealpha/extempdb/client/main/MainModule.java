/*
 * MainModule.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.client.main;

import com.google.gwt.inject.client.AbstractGinModule;

public class MainModule extends AbstractGinModule {
	@Override
	protected void configure() {
		bind(LoginPresenter.Display.class).to(LoginWidget.class);
	}
}
