/*
 * LoginPresenter.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.client.main;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.prealpha.dispatch.shared.DispatcherAsync;
import com.prealpha.extempdb.client.Presenter;
import com.prealpha.extempdb.client.SessionManager;
import com.prealpha.extempdb.client.error.ManagedCallback;
import com.prealpha.extempdb.shared.action.GetSessionResult;
import com.prealpha.extempdb.shared.action.LogIn;
import com.prealpha.extempdb.shared.dto.UserSessionDto;

public class LoginPresenter implements Presenter<UserSessionDto> {
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

	private final MainMessages messages;

	@Inject
	public LoginPresenter(Display display, DispatcherAsync dispatcher,
			SessionManager sessionManager, MainMessages messages) {
		this.display = display;
		this.dispatcher = dispatcher;
		this.sessionManager = sessionManager;
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
	public void bind(UserSessionDto session) {
		if (session == null) {
			display.setVisible(true);
			display.getNameBox().setText(null);
			display.getPasswordBox().setText(null);
			display.getStatusLabel().setText(messages.logInText());
		} else {
			display.setVisible(false);
		}
	}

	private void submit() {
		String name = display.getNameBox().getText();
		String password = display.getPasswordBox().getText();
		LogIn action = new LogIn(name, password);

		dispatcher.execute(action, new ManagedCallback<GetSessionResult>() {
			@Override
			public void onSuccess(GetSessionResult result) {
				UserSessionDto session = result.getSession();

				if (session == null) {
					display.getStatusLabel().setText(
							messages.logInTextInvalid());
				} else {
					sessionManager.setSession(session);
				}
			}
		});

		display.getStatusLabel().setText(messages.logInTextPending());
	}
}
