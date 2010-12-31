/*
 * LoginStatusPresenter.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.client.core;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.prealpha.extempdb.client.Presenter;
import com.prealpha.extempdb.client.SessionManager;
import com.prealpha.extempdb.shared.dto.UserDto;
import com.prealpha.extempdb.shared.dto.UserSessionDto;

public class LoginStatusPresenter implements Presenter<UserSessionDto> {
	public static interface Display extends IsWidget {
		HasHTML getStatusLabel();

		HasClickHandlers getLogOutLink();
	}

	private final Display display;

	private final CoreMessages messages;

	@Inject
	public LoginStatusPresenter(Display display, CoreMessages messages,
			final SessionManager sessionManager) {
		this.display = display;
		this.messages = messages;

		this.display.getLogOutLink().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				sessionManager.setSession(null);
			}
		});
	}

	@Override
	public Display getDisplay() {
		return display;
	}

	@Override
	public void bind(UserSessionDto session) {
		String html;
		boolean showLinks;

		if (session == null) {
			html = messages.notLoggedIn();
			showLinks = false;
		} else {
			UserDto user = session.getUser();
			String name = user.getName();
			html = messages.loggedIn(name);
			showLinks = true;
		}

		display.getStatusLabel().setHTML(html);
		display.asWidget().setVisible(showLinks);
	}
}
