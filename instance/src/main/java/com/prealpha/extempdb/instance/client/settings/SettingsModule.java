/*
 * SettingsModule.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.client.settings;

import com.google.gwt.inject.client.AbstractGinModule;

public class SettingsModule extends AbstractGinModule {
	public SettingsModule() {
	}

	@Override
	protected void configure() {
		bind(SettingsPresenter.Display.class).to(SettingsWidget.class);
		bind(AddArticlePresenter.Display.class).to(AddArticleWidget.class);
	}
}
