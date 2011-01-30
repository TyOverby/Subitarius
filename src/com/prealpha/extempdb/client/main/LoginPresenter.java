/*
 * LoginPresenter.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.client.main;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.prealpha.dispatch.shared.DispatcherAsync;
import com.prealpha.extempdb.client.Presenter;
import com.prealpha.extempdb.client.SessionManager;
import com.prealpha.extempdb.client.error.ManagedCallback;
import com.prealpha.extempdb.client.event.ActiveUserEvent;
import com.prealpha.extempdb.shared.action.GetUserResult;
import com.prealpha.extempdb.shared.action.LogIn;
import com.prealpha.extempdb.shared.dto.UserDto;

public class LoginPresenter implements Presenter<UserDto> {
	public static interface Display extends IsWidget {
		boolean isVisible();

		void setVisible(boolean visible);

		HasText getNameBox();

		HasText getPasswordBox();

		HasClickHandlers getSubmitButton();

		HasText getStatusLabel();
	}

	private final Display display;

	private final DispatcherAsync dispatcher;

	private final SessionManager sessionManager;

	private final EventBus eventBus;

	private final MainMessages messages;

	@Inject
	public LoginPresenter(Display display, DispatcherAsync dispatcher,
			SessionManager sessionManager, EventBus eventBus,
			MainMessages messages) {
		this.display = display;
		this.dispatcher = dispatcher;
		this.sessionManager = sessionManager;
		this.eventBus = eventBus;
		this.messages = messages;

		display.setVisible(false);
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
	public void bind(UserDto user) {
		if (user == null) {
			display.setVisible(true);
			display.getNameBox().setText(null);
			display.getPasswordBox().setText(null);
			display.getStatusLabel().setText(messages.logInText());
		} else {
			display.setVisible(false);
		}
	}

	private void submit() {
		String sessionId = sessionManager.getSessionId();

		if (sessionId == null) {
			Window.alert(messages.noCookies());
			return;
		}

		String name = display.getNameBox().getText();
		String password = display.getPasswordBox().getText();
		LogIn action = new LogIn(sessionId, name, password);

		dispatcher.execute(action, new ManagedCallback<GetUserResult>() {
			@Override
			public void onSuccess(GetUserResult result) {
				UserDto user = result.getUser();

				if (user == null) {
					display.getStatusLabel().setText(
							messages.logInTextInvalid());
				} else {
					eventBus.fireEventFromSource(new ActiveUserEvent(user),
							LoginPresenter.this);
				}
			}
		});

		display.getStatusLabel().setText(messages.logInTextPending());
	}
}
