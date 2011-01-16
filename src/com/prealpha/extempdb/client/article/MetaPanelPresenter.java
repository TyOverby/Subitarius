/*
 * MetaPanelPresenter.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.client.article;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.prealpha.dispatch.shared.DispatcherAsync;
import com.prealpha.extempdb.client.Presenter;
import com.prealpha.extempdb.client.error.ManagedCallback;
import com.prealpha.extempdb.shared.action.GetMapping;
import com.prealpha.extempdb.shared.action.GetMappingResult;
import com.prealpha.extempdb.shared.action.GetMappingsByArticle;
import com.prealpha.extempdb.shared.action.GetMappingsResult;
import com.prealpha.extempdb.shared.dto.ArticleDto;
import com.prealpha.extempdb.shared.dto.TagMappingDto;
import com.prealpha.extempdb.shared.dto.TagMappingDto.State;
import com.prealpha.extempdb.shared.id.TagMappingId;

/*
 * TODO: relies on DateTimeFormat, so would require GwtTestCase to unit test
 */
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

	private final DateTimeFormat dateTimeFormat;

	private final MappingInputPresenter mappingInputPresenter;

	private final Provider<TagMappingPresenter> mappingPresenterProvider;

	@Inject
	public MetaPanelPresenter(Display display, DispatcherAsync dispatcher,
			DateTimeFormat dateTimeFormat,
			MappingInputPresenter mappingInputPresenter,
			Provider<TagMappingPresenter> mappingPresenterProvider) {
		this.display = display;
		this.dispatcher = dispatcher;
		this.dateTimeFormat = dateTimeFormat;
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
		display.getDateLabel()
				.setText(dateTimeFormat.format(article.getDate()));
		display.getRetrievalDateLabel().setText(
				dateTimeFormat.format(article.getRetrievalDate()));
		display.getTagsPanel().clear();

		mappingInputPresenter.bind(article);

		GetMappingsByArticle action = new GetMappingsByArticle(article);
		dispatcher.execute(action, new MappingsCallback());
	}

	private class MappingsCallback extends ManagedCallback<GetMappingsResult> {
		@Override
		public void onSuccess(GetMappingsResult result) {
			for (TagMappingId id : result.getIds()) {
				GetMapping action = new GetMapping(id);
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
