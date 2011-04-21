/*
 * SessionManager.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.client;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.prealpha.dispatch.shared.DispatcherAsync;
import com.prealpha.extempdb.client.error.ManagedCallback;
import com.prealpha.extempdb.client.event.ActiveUserEvent;
import com.prealpha.extempdb.shared.action.GetUser;
import com.prealpha.extempdb.shared.action.GetUserResult;
import com.prealpha.extempdb.shared.dto.UserDto;

public final class SessionManager {
	private static final String COOKIE_NAME = "JSESSIONID";

	private final EventBus eventBus;

	private final DispatcherAsync dispatcher;

	@Inject
	public SessionManager(EventBus eventBus, DispatcherAsync dispatcher) {
		this.eventBus = eventBus;
		this.dispatcher = dispatcher;
	}

	public String getSessionId() {
		return Cookies.getCookie(COOKIE_NAME);
	}

	public void getActiveUser(final AsyncCallback<UserDto> callback) {
		String sessionId = getSessionId();

		if (sessionId == null) {
			callback.onSuccess(null);
			fire(null);
		} else {
			GetUser action = new GetUser(sessionId);
			dispatcher.execute(action, new AsyncCallback<GetUserResult>() {
				@Override
				public void onFailure(Throwable caught) {
					callback.onFailure(caught);
					fire(null);
				}

				@Override
				public void onSuccess(GetUserResult result) {
					callback.onSuccess(result.getUser());
					fire(result.getUser());
				}
			});
		}
	}

	public void fireActiveUser() {
		getActiveUser(new ManagedCallback<UserDto>() {
			@Override
			public void onSuccess(UserDto result) {
				fire(result);
			}
		});
	}

	private void fire(UserDto user) {
		eventBus.fireEventFromSource(new ActiveUserEvent(user), this);
	}
}
