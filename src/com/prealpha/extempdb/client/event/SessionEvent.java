/*
 * SessionEvent.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.prealpha.extempdb.shared.dto.UserSessionDto;

public class SessionEvent extends GwtEvent<SessionHandler> {
	private static final Type<SessionHandler> TYPE = new Type<SessionHandler>();

	public static Type<SessionHandler> getType() {
		return TYPE;
	}

	private final UserSessionDto session;

	public SessionEvent(UserSessionDto session) {
		this.session = session;
	}

	public UserSessionDto getSession() {
		return session;
	}

	@Override
	public Type<SessionHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SessionHandler handler) {
		handler.sessionUpdated(this);
	}
}
