/*
 * ArticlePresenter.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.instance.client.article;

import static com.google.common.base.Preconditions.*;

import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.prealpha.dispatch.shared.DispatcherAsync;
import com.subitarius.action.GetArticleByHash;
import com.subitarius.action.GetArticleResult;
import com.subitarius.action.dto.ArticleDto;
import com.subitarius.instance.client.HistoryManager;
import com.subitarius.instance.client.PlacePresenter;
import com.subitarius.instance.client.Presenter;
import com.subitarius.instance.client.error.ManagedCallback;

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
			String articleHash = parameters.get(0);
			GetArticleByHash action = new GetArticleByHash(articleHash);
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
