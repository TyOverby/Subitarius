/*
 * ArticleWidget.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.instance.client.article;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.subitarius.action.dto.ArticleDto;
import com.subitarius.instance.client.Presenter;

public class ArticleWidget extends Composite implements
		ArticlePresenter.Display {
	public static interface ArticleUiBinder extends
			UiBinder<Widget, ArticleWidget> {
	}

	@UiField(provided = true)
	final Widget metaPanel;

	@UiField(provided = true)
	final Widget articleDisplay;

	private final MetaPanelPresenter metaPresenter;

	private final ArticleDisplayPresenter articlePresenter;

	@Inject
	public ArticleWidget(ArticleUiBinder uiBinder,
			MetaPanelPresenter metaPresenter,
			ArticleDisplayPresenter articlePresenter) {
		this.metaPresenter = metaPresenter;
		this.articlePresenter = articlePresenter;

		metaPanel = metaPresenter.getDisplay().asWidget();
		articleDisplay = articlePresenter.getDisplay().asWidget();

		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public Presenter<ArticleDto> getMetaPresenter() {
		return metaPresenter;
	}

	@Override
	public Presenter<ArticleDto> getArticlePresenter() {
		return articlePresenter;
	}
}
