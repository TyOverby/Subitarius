/*
 * ArticleDisplayPresenter.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.instance.client.article;

import java.util.List;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.prealpha.dispatch.shared.DispatcherAsync;
import com.subitarius.action.GetParagraphs;
import com.subitarius.action.GetParagraphsResult;
import com.subitarius.action.dto.ArticleDto;
import com.subitarius.instance.client.Presenter;
import com.subitarius.instance.client.error.ManagedCallback;

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
		display.getTitleLink().setHref(article.getUrl().getUrl());
		display.getBylineLabel().setText(article.getByline());
		display.getSourceLabel().setText(article.getUrl().getSource());

		GetParagraphs action = new GetParagraphs(article.getHash());
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
