/*
 * JumpModule.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.instance.client.jump;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.view.client.ProvidesKey;
import com.google.inject.Provides;
import com.subitarius.instance.shared.dto.ArticleDto;

public class JumpModule extends AbstractGinModule {
	public JumpModule() {
	}

	protected void configure() {
		bind(JumpPresenter.Display.class).to(JumpWidget.class);
		bind(ArticleTablePresenter.Display.class).to(ArticleTableWidget.class);
	}

	@Provides
	CellTable<ArticleDto> getCellTable() {
		return new CellTable<ArticleDto>(new ProvidesKey<ArticleDto>() {
			@Override
			public Object getKey(ArticleDto article) {
				return (article == null ? null : article.getHash());
			}
		});
	}
}
