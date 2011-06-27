/*
 * SettingsModule.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.client.settings;

import com.google.gwt.inject.client.AbstractGinModule;

public class SettingsModule extends AbstractGinModule {
	public SettingsModule() {
	}

	@Override
	protected void configure() {
		bind(SettingsPresenter.Display.class).to(SettingsWidget.class);
		bind(TagManagerPresenter.Display.class).to(TagManagerWidget.class);
		bind(PasswordChangePresenter.Display.class).to(
				PasswordChangeWidget.class);
		bind(AddArticlePresenter.Display.class).to(AddArticleWidget.class);
	}
}