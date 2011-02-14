/*
 * AddArticlePresenter.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.client.settings;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HasClickHandlers;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.prealpha.dispatch.shared.DispatcherAsync;
import com.prealpha.extempdb.client.AppPlace;
import com.prealpha.extempdb.client.AppState;
import com.prealpha.extempdb.client.HistoryManager;
import com.prealpha.extempdb.client.SessionManager;
import com.prealpha.extempdb.client.Presenter;
import com.prealpha.extempdb.client.error.ManagedCallback;
import com.prealpha.extempdb.shared.action.AddArticle;
import com.prealpha.extempdb.shared.action.AddArticleResult;

/*
 * TODO: doesn't present anything
 */
class AddArticlePresenter implements Presenter<Void> {
	static interface Display extends IsWidget {
		HasTest getStatusLabel();
		
		HasText getUrlBox();
		
		HasClickHandlers getAddButton();
	}
	
	private final Display display;
	
	private final DispatcherAsync dispatcher;
	
	private final SessionManager sessionManager;
	
	private final HistoryManager historyManager;
	
	private final SettingsMessages messages;
	
	@Inject
	public AddArticlePresenter(Display display, DispatcherAsync dispatcher, SessionManager sessionManager, HistoryManager historyManager, SettingsMessages messages) {
		this.display = display;
		this.dispatcher = dispatcher;
		this.sessionManager = sessionManager;
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
		String sessionId = sessionManager.getSessionId();
		String url = display.getUrlBox().getText();
		AddArticle action = new AddArticle(sessionId, url);
		dispatcher.execute(action, new ManagedCallback<AddArticleResult>() {
			@Override
			public void onSuccess(AddArticleResult result) {
				if (result.isSuccess()) {
					Long articleId = result.getArticleId();
					AppState appState = new AppState(AppPlace.ARTICLE, articleId);
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
					case NO_PERMISSION:
						message = messages.notLoggedIn();
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
