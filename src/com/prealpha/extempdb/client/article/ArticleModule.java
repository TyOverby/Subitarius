/*
 * ArticleModule.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.client.article;

import com.google.gwt.inject.client.AbstractGinModule;

public class ArticleModule extends AbstractGinModule {
	@Override
	protected void configure() {
		bind(ArticleDisplayPresenter.Display.class).to(
				ArticleDisplayWidget.class);
		bind(MetaPanelPresenter.Display.class).to(MetaPanelWidget.class);
		bind(TagMappingPresenter.Display.class).to(TagMappingWidget.class);
	}
}
