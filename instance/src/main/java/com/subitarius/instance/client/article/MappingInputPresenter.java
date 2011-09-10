/*
 * MappingInputPresenter.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.instance.client.article;

import static com.google.common.base.Preconditions.*;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.prealpha.dispatch.shared.DispatcherAsync;
import com.subitarius.action.AddMapping;
import com.subitarius.action.MutationResult;
import com.subitarius.action.dto.ArticleDto;
import com.subitarius.action.dto.TagDto;
import com.subitarius.action.dto.TagMappingDto.State;
import com.subitarius.instance.client.Presenter;
import com.subitarius.instance.client.error.ManagedCallback;

public final class MappingInputPresenter implements Presenter<ArticleDto> {
	public static interface Display extends IsWidget {
		HasValue<TagDto> getTagInput();

		HasClickHandlers getSubmitButton();
	}

	private final Display display;

	private final DispatcherAsync dispatcher;

	private ArticleDto article;

	@Inject
	private MappingInputPresenter(final Display display,
			DispatcherAsync dispatcher, EventBus eventBus) {
		this.display = display;
		this.dispatcher = dispatcher;
		display.getSubmitButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				submit();
			}
		});
	}

	@Override
	public Display getDisplay() {
		return display;
	}

	@Override
	public void bind(ArticleDto article) {
		checkNotNull(article);
		this.article = article;
	}

	private void submit() {
		HasValue<TagDto> tagInput = display.getTagInput();
		TagDto tag = tagInput.getValue();
		if (tag != null) {
			String tagName = tag.getName();
			String articleUrlHash = article.getUrl().getHash();

			AddMapping action = new AddMapping(tagName, articleUrlHash,
					State.PATROLLED);
			dispatcher.execute(action, new ManagedCallback<MutationResult>() {
				@Override
				public void onSuccess(MutationResult result) {
					// TODO: on success, we shouldn't need to reload
					Window.Location.reload();
				}
			});
		}
	}
}
