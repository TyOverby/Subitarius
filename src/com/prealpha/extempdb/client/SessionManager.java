/*
 * SessionManager.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.client;

import java.util.Date;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.prealpha.extempdb.client.error.ManagedCallback;
import com.prealpha.extempdb.client.event.SessionEvent;
import com.prealpha.extempdb.shared.action.GetSession;
import com.prealpha.extempdb.shared.action.GetSessionResult;
import com.prealpha.extempdb.shared.dto.UserSessionDto;
import com.prealpha.extempdb.shared.id.UserSessionToken;
import com.prealpha.gwt.dispatch.shared.DispatcherAsync;

public final class SessionManager {
	private static final String COOKIE_NAME = "EXTEMPDB";

	private final EventBus eventBus;

	private final DispatcherAsync service;

	@Inject
	public SessionManager(EventBus eventBus, DispatcherAsync service) {
		this.eventBus = eventBus;
		this.service = service;
	}

	public UserSessionToken getSessionToken() {
		String token = Cookies.getCookie(COOKIE_NAME);

		if (token == null) {
			return null;
		} else {
			return new UserSessionToken(token);
		}
	}

	public void getSession(final AsyncCallback<UserSessionDto> callback) {
		String token = Cookies.getCookie(COOKIE_NAME);

		if (token == null) {
			callback.onSuccess(null);
			setSession(null);
			return;
		}

		GetSession action = new GetSession(new UserSessionToken(token));
		service.execute(action, new AsyncCallback<GetSessionResult>() {
			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
				setSession(null);
			}

			@Override
			public void onSuccess(GetSessionResult result) {
				UserSessionDto session = result.getSession();
				callback.onSuccess(session);
				setSession(session);
			}
		});
	}

	public void setSession(UserSessionDto session) {
		if (session == null) {
			Cookies.removeCookie(COOKIE_NAME);
		} else {
			String token = session.getToken();
			Date expiry = session.getExpiry();

			Cookies.setCookie(COOKIE_NAME, token, expiry);
		}

		eventBus.fireEventFromSource(new SessionEvent(session), this);
	}

	public void fireCurrentSession() {
		getSession(new ManagedCallback<UserSessionDto>() {
			@Override
			public void onSuccess(UserSessionDto result) {
			}
		});
	}
}
