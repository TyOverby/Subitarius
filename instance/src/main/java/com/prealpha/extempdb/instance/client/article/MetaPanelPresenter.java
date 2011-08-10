/*
 * MetaPanelPresenter.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.client.article;

import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.prealpha.dispatch.shared.DispatcherAsync;
import com.prealpha.extempdb.instance.client.Presenter;
import com.prealpha.extempdb.instance.client.error.ManagedCallback;
import com.prealpha.extempdb.instance.shared.action.GetMapping;
import com.prealpha.extempdb.instance.shared.action.GetMappingResult;
import com.prealpha.extempdb.instance.shared.action.GetMappingsByArticle;
import com.prealpha.extempdb.instance.shared.action.GetMappingsResult;
import com.prealpha.extempdb.instance.shared.dto.ArticleDto;
import com.prealpha.extempdb.instance.shared.dto.TagMappingDto;
import com.prealpha.extempdb.instance.shared.dto.TagMappingDto.State;

public class MetaPanelPresenter implements Presenter<ArticleDto> {
	public static interface Display extends IsWidget {
		HasText getIdLabel();

		HasText getDateLabel();

		HasText getRetrievalDateLabel();

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
		display.getIdLabel().setText(article.getId().toString());
		display.getDateLabel().setText(article.getDate());
		display.getRetrievalDateLabel().setText(article.getRetrievalDate());
		display.getTagsPanel().clear();

		mappingInputPresenter.bind(article);

		GetMappingsByArticle action = new GetMappingsByArticle(article.getId());
		dispatcher.execute(action, new MappingsCallback());
	}

	private class MappingsCallback extends ManagedCallback<GetMappingsResult> {
		@Override
		public void onSuccess(GetMappingsResult result) {
			for (TagMappingDto.Key mappingKey : result.getMappingKeys()) {
				GetMapping action = new GetMapping(mappingKey);
				dispatcher.execute(action, new MappingCallback());
			}
		}
	}

	private class MappingCallback extends ManagedCallback<GetMappingResult> {
		@Override
		public void onSuccess(GetMappingResult result) {
			TagMappingDto mapping = result.getMapping();

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
