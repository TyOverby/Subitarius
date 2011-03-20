/*
 * TagInputModule.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.client.taginput;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.user.client.ui.SuggestOracle;

public class TagInputModule extends AbstractGinModule {
	public TagInputModule() {
	}

	@Override
	protected void configure() {
		bind(TagInputPresenter.Display.class).to(TagInputWidget.class);
		bind(LoadingStatusPresenter.Display.class)
				.to(LoadingStatusWidget.class);
		bind(SuggestOracle.class).to(TagInputSuggestOracle.class);
		requestStaticInjection(LoadingStatus.class);
	}
}
