/*
 * BrowseModule.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.client.browse;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.view.client.ProvidesKey;
import com.google.inject.Provides;
import com.prealpha.extempdb.shared.dto.ArticleDto;

public class BrowseModule extends AbstractGinModule {
	public BrowseModule() {
	}

	protected void configure() {
		bind(BrowsePresenter.Display.class).to(BrowseWidget.class);
		bind(ArticleTablePresenter.Display.class).to(ArticleTableWidget.class);
	}

	@Provides
	CellTable<ArticleDto> getCellTable() {
		return new CellTable<ArticleDto>(new ProvidesKey<ArticleDto>() {
			@Override
			public Object getKey(ArticleDto article) {
				return (article == null ? null : article.getId());
			}
		});
	}
}
