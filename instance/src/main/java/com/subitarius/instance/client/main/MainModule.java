/*
 * MainModule.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.instance.client.main;

import com.google.gwt.inject.client.AbstractGinModule;

public class MainModule extends AbstractGinModule {
	public MainModule() {
	}

	@Override
	protected void configure() {
		bind(MainPresenter.Display.class).to(MainWidget.class);
		bind(AddArticlePresenter.Display.class).to(AddArticleWidget.class);
	}
}
