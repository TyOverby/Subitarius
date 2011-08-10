/*
 * ArticlePresenter.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.client.article;

import static com.google.common.base.Preconditions.*;

import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.prealpha.dispatch.shared.DispatcherAsync;
import com.prealpha.extempdb.instance.client.HistoryManager;
import com.prealpha.extempdb.instance.client.PlacePresenter;
import com.prealpha.extempdb.instance.client.Presenter;
import com.prealpha.extempdb.instance.client.error.ManagedCallback;
import com.prealpha.extempdb.instance.shared.action.GetArticle;
import com.prealpha.extempdb.instance.shared.action.GetArticleResult;
import com.prealpha.extempdb.instance.shared.dto.ArticleDto;

public class ArticlePresenter implements PlacePresenter {
	public static interface Display extends IsWidget {
		Presenter<ArticleDto> getMetaPresenter();

		Presenter<ArticleDto> getArticlePresenter();
	}

	private final Display display;

	private final DispatcherAsync dispatcher;

	@Inject
	public ArticlePresenter(Display display, DispatcherAsync dispatcher,
			HistoryManager historyManager) {
		this.display = display;
		this.dispatcher = dispatcher;
	}

	@Override
	public void init() {
	}

	@Override
	public Display getDisplay() {
		return display;
	}

	@Override
	public void bind(List<String> parameters) {
		checkArgument(parameters.size() == 1);

		try {
			Long articleId = Long.parseLong(parameters.get(0));
			GetArticle action = new GetArticle(articleId);
			dispatcher.execute(action, new ManagedCallback<GetArticleResult>() {
				@Override
				public void onSuccess(GetArticleResult result) {
					ArticleDto article = result.getArticle();
					display.getMetaPresenter().bind(article);
					display.getArticlePresenter().bind(article);
				}
			});
		} catch (NumberFormatException nfx) {
			throw new IllegalArgumentException(nfx);
		}
	}

	@Override
	public void destroy() {
	}
}
