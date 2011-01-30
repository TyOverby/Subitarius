/*
 * LoginStatusPresenter.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.client.core;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.prealpha.dispatch.shared.DispatcherAsync;
import com.prealpha.extempdb.client.Presenter;
import com.prealpha.extempdb.client.SessionManager;
import com.prealpha.extempdb.client.error.ManagedCallback;
import com.prealpha.extempdb.client.event.ActiveUserEvent;
import com.prealpha.extempdb.shared.action.LogOut;
import com.prealpha.extempdb.shared.action.MutationResult;
import com.prealpha.extempdb.shared.dto.UserDto;

public class LoginStatusPresenter implements Presenter<UserDto> {
	public static interface Display extends IsWidget {
		HasHTML getStatusLabel();

		HasClickHandlers getLogOutLink();
	}

	private final Display display;

	private final DispatcherAsync dispatcher;

	private final SessionManager sessionManager;

	private final EventBus eventBus;

	private final CoreMessages messages;

	@Inject
	public LoginStatusPresenter(Display display, DispatcherAsync dispatcher,
			SessionManager sessionManager, EventBus eventBus,
			CoreMessages messages) {
		this.display = display;
		this.dispatcher = dispatcher;
		this.sessionManager = sessionManager;
		this.eventBus = eventBus;
		this.messages = messages;

		display.getLogOutLink().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				logOut();
			}
		});
	}

	@Override
	public Display getDisplay() {
		return display;
	}

	@Override
	public void bind(UserDto user) {
		String html;
		boolean showLinks;

		if (user == null) {
			html = messages.notLoggedIn();
			showLinks = false;
		} else {
			String name = user.getName();
			html = messages.loggedIn(name);
			showLinks = true;
		}

		display.getStatusLabel().setHTML(html);
		display.asWidget().setVisible(showLinks);
	}

	private void logOut() {
		String sessionId = sessionManager.getSessionId();

		if (sessionId != null) {
			LogOut action = new LogOut(sessionId);
			dispatcher.execute(action, new ManagedCallback<MutationResult>() {
				@Override
				public void onSuccess(MutationResult result) {
				}
			});
			eventBus.fireEventFromSource(new ActiveUserEvent(null), this);
		}
	}
}
