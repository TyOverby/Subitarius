/*
 * ArticleDisplayPresenter.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.client.article;

import java.util.List;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.prealpha.dispatch.shared.DispatcherAsync;
import com.prealpha.extempdb.client.Presenter;
import com.prealpha.extempdb.client.error.ManagedCallback;
import com.prealpha.extempdb.shared.action.GetParagraphs;
import com.prealpha.extempdb.shared.action.GetParagraphsResult;
import com.prealpha.extempdb.shared.dto.ArticleDto;

public class ArticleDisplayPresenter implements Presenter<ArticleDto> {
	public static interface Display extends IsWidget {
		Anchor getTitleLink();

		HasText getBylineLabel();

		HasText getSourceLabel();

		HasWidgets getArticleText();
	}

	private final Display display;

	private final DispatcherAsync dispatcher;

	@Inject
	public ArticleDisplayPresenter(Display display, DispatcherAsync dispatcher) {
		this.display = display;
		this.dispatcher = dispatcher;
	}

	@Override
	public Display getDisplay() {
		return display;
	}

	@Override
	public void bind(ArticleDto article) {
		display.getTitleLink().setText(article.getTitle());
		display.getTitleLink().setHref(article.getUrl());
		display.getBylineLabel().setText(article.getByline());
		display.getSourceLabel().setText(article.getSource().getDisplayName());

		GetParagraphs action = new GetParagraphs(article);
		dispatcher.execute(action, new ManagedCallback<GetParagraphsResult>() {
			@Override
			public void onSuccess(GetParagraphsResult result) {
				updateText(result.getParagraphs());
			}
		});
	}

	private void updateText(List<String> paragraphs) {
		display.getArticleText().clear();

		for (String paragraph : paragraphs) {
			HTML html = new HTML();
			html.setText(paragraph);
			html.setHTML("<p>" + html.getHTML() + "</p>");
			display.getArticleText().add(html);
		}
	}
}
