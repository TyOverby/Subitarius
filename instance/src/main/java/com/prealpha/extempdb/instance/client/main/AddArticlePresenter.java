/*
 * AddArticlePresenter.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.client.main;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.prealpha.dispatch.shared.DispatcherAsync;
import com.prealpha.extempdb.instance.client.AppPlace;
import com.prealpha.extempdb.instance.client.AppState;
import com.prealpha.extempdb.instance.client.HistoryManager;
import com.prealpha.extempdb.instance.client.Presenter;
import com.prealpha.extempdb.instance.client.error.ManagedCallback;
import com.prealpha.extempdb.instance.shared.action.AddArticle;
import com.prealpha.extempdb.instance.shared.action.AddArticleResult;

/*
 * TODO: doesn't present anything
 */
public class AddArticlePresenter implements Presenter<Void> {
	public static interface Display extends IsWidget {
		HasText getStatusLabel();

		HasText getUrlBox();

		HasClickHandlers getAddButton();
	}

	private final Display display;

	private final DispatcherAsync dispatcher;

	private final HistoryManager historyManager;

	private final MainMessages messages;

	@Inject
	public AddArticlePresenter(Display display, DispatcherAsync dispatcher,
			HistoryManager historyManager, MainMessages messages) {
		this.display = display;
		this.dispatcher = dispatcher;
		this.historyManager = historyManager;
		this.messages = messages;

		display.getAddButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				add();
			}
		});
	}

	@Override
	public Display getDisplay() {
		return display;
	}

	@Override
	public void bind(Void v) {
	}

	private void add() {
		String url = display.getUrlBox().getText();
		AddArticle action = new AddArticle(url);
		dispatcher.execute(action, new ManagedCallback<AddArticleResult>() {
			@Override
			public void onSuccess(AddArticleResult result) {
				if (result.getType().isSuccess()) {
					String articleHash = result.getArticleHash();
					List<String> parameters = ImmutableList.of(articleHash);
					AppState appState = new AppState(AppPlace.ARTICLE,
							parameters);
					historyManager.setAppState(appState);
				} else {
					String message;
					switch (result.getType()) {
					case INVALID_URL:
						message = messages.statusLabelInvalid();
						break;
					case NO_PARSER:
						message = messages.statusLabelNoParser();
						break;
					case NO_ARTICLE:
						message = messages.statusLabelNoArticle();
						break;
					case PARSE_FAILED:
						message = messages.statusLabelFailed();
						break;
					default:
						throw new IllegalStateException();
					}
					display.getStatusLabel().setText(message);
				}
			}
		});
	}
}
