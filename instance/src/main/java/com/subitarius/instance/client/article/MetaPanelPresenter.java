/*
 * MetaPanelPresenter.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.instance.client.article;

import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.prealpha.dispatch.shared.DispatcherAsync;
import com.subitarius.instance.client.Presenter;
import com.subitarius.instance.client.error.ManagedCallback;
import com.subitarius.instance.shared.action.GetMappingsByArticle;
import com.subitarius.instance.shared.action.GetMappingsResult;
import com.subitarius.instance.shared.dto.ArticleDto;
import com.subitarius.instance.shared.dto.TagMappingDto;
import com.subitarius.instance.shared.dto.TagMappingDto.State;

public class MetaPanelPresenter implements Presenter<ArticleDto> {
	public static interface Display extends IsWidget {
		HasHTML getHashLabel();

		HasText getDateLabel();

		HasText getSearchDateLabel();

		HasText getParseDateLabel();

		HasWidgets getTagsPanel();

		HasWidgets getMappingInputPanel();
	}

	private final Display display;

	private final DispatcherAsync dispatcher;

	private final MappingInputPresenter mappingInputPresenter;

	private final Provider<TagMappingPresenter> mappingPresenterProvider;

	@Inject
	public MetaPanelPresenter(Display display, DispatcherAsync dispatcher,
			MappingInputPresenter mappingInputPresenter,
			Provider<TagMappingPresenter> mappingPresenterProvider) {
		this.display = display;
		this.dispatcher = dispatcher;
		this.mappingInputPresenter = mappingInputPresenter;
		this.mappingPresenterProvider = mappingPresenterProvider;

		display.getMappingInputPanel().add(
				mappingInputPresenter.getDisplay().asWidget());
	}

	@Override
	public Display getDisplay() {
		return display;
	}

	@Override
	public void bind(ArticleDto article) {
		String hash = article.getHash();
		String hashHtml = "";
		hashHtml += hash.substring(0, 24) + "<br />";
		hashHtml += hash.substring(24, 48) + "<br />";
		hashHtml += hash.substring(48, 64) + "<br />";

		display.getHashLabel().setHTML(hashHtml);
		display.getDateLabel().setText(article.getDate());
		display.getSearchDateLabel().setText(article.getUrl().getCreateDate());
		display.getParseDateLabel().setText(article.getCreateDate());
		display.getTagsPanel().clear();

		mappingInputPresenter.bind(article);

		GetMappingsByArticle action = new GetMappingsByArticle(article.getUrl()
				.getHash());
		dispatcher.execute(action, new MappingsCallback());
	}

	private class MappingsCallback extends ManagedCallback<GetMappingsResult> {
		@Override
		public void onSuccess(GetMappingsResult result) {
			for (TagMappingDto mapping : result.getMappings()) {
				if (!mapping.getState().equals(State.REMOVED)) {
					TagMappingPresenter mappingPresenter = mappingPresenterProvider
							.get();
					mappingPresenter.bind(mapping);
					display.getTagsPanel().add(
							mappingPresenter.getDisplay().asWidget());
				}
			}
		}
	}
}
