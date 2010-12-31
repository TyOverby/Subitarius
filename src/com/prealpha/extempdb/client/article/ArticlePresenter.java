/*
 * ArticlePresenter.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.client.article;

import static com.google.common.base.Preconditions.*;

import java.util.List;

import com.google.inject.Inject;
import com.prealpha.extempdb.client.HistoryManager;
import com.prealpha.extempdb.client.PlacePresenter;
import com.prealpha.extempdb.client.error.ManagedCallback;
import com.prealpha.extempdb.shared.action.GetArticle;
import com.prealpha.extempdb.shared.action.GetArticleResult;
import com.prealpha.extempdb.shared.dto.ArticleDto;
import com.prealpha.extempdb.shared.id.ArticleId;
import com.prealpha.gwt.dispatch.shared.DispatcherAsync;

public class ArticlePresenter implements PlacePresenter {
	private final ArticleWidget widget;

	private final DispatcherAsync dispatcher;

	@Inject
	public ArticlePresenter(ArticleWidget widget, DispatcherAsync dispatcher,
			HistoryManager historyManager) {
		this.widget = widget;
		this.dispatcher = dispatcher;
	}

	@Override
	public void init() {
	}

	@Override
	public ArticleWidget getDisplay() {
		return widget;
	}

	@Override
	public void bind(List<String> parameters) {
		checkArgument(parameters.size() == 1);

		ArticleId id = new ArticleId(Long.parseLong(parameters.get(0)));
		GetArticle action = new GetArticle(id);
		dispatcher.execute(action, new ManagedCallback<GetArticleResult>() {
			@Override
			public void onSuccess(GetArticleResult result) {
				ArticleDto article = result.getArticle();
				widget.getMetaPresenter().bind(article);
				widget.getArticlePresenter().bind(article);
			}
		});
	}

	@Override
	public void destroy() {
	}
}
