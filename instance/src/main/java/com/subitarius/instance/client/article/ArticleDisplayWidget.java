/*
 * ArticleDisplayWidget.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.instance.client.article;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class ArticleDisplayWidget extends Composite implements
		ArticleDisplayPresenter.Display {
	public static interface ArticleDisplayUiBinder extends
			UiBinder<Widget, ArticleDisplayWidget> {
	}

	@UiField
	Anchor titleLink;

	@UiField
	HasText bylineLabel;

	@UiField
	HasText sourceLabel;

	@UiField
	HasWidgets articleText;

	@Inject
	public ArticleDisplayWidget(ArticleDisplayUiBinder uiBinder) {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public Anchor getTitleLink() {
		return titleLink;
	}

	@Override
	public HasText getBylineLabel() {
		return bylineLabel;
	}

	@Override
	public HasText getSourceLabel() {
		return sourceLabel;
	}

	@Override
	public HasWidgets getArticleText() {
		return articleText;
	}
}
