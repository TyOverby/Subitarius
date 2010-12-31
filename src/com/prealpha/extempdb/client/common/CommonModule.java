/*
 * CommonModule.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.client.common;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.user.client.ui.SuggestOracle;

public class CommonModule extends AbstractGinModule {
	@Override
	protected void configure() {
		bind(LoadingStatusPresenter.Display.class)
				.to(LoadingStatusWidget.class);
		bind(SuggestOracle.class).to(TagInputSuggestOracle.class);
		requestStaticInjection(LoadingStatus.class);
	}
}
