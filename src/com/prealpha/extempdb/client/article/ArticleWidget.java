/*
 * ArticleWidget.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.client.article;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class ArticleWidget extends Composite {
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

	public MetaPanelPresenter getMetaPresenter() {
		return metaPresenter;
	}

	public ArticleDisplayPresenter getArticlePresenter() {
		return articlePresenter;
	}
}
