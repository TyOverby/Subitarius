/*
 * TagInputModule.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.client.taginput;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.inject.Inject;
import com.google.inject.Provides;

public final class TagInputModule extends AbstractGinModule {
	public TagInputModule() {
	}

	@Override
	protected void configure() {
		bind(SuggestOracle.class).to(TagInputSuggestOracle.class);
		requestStaticInjection(LoadingStatus.class);
	}

	@Provides
	@Inject
	SuggestBox getSuggestBox(SuggestOracle oracle) {
		return new SuggestBox(oracle);
	}
}
