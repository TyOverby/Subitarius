/*
 * ActiveUserEvent.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.prealpha.extempdb.instance.shared.dto.UserDto;

public class ActiveUserEvent extends GwtEvent<ActiveUserHandler> {
	private static final Type<ActiveUserHandler> TYPE = new Type<ActiveUserHandler>();

	public static Type<ActiveUserHandler> getType() {
		return TYPE;
	}

	private final UserDto user;

	public ActiveUserEvent(UserDto user) {
		this.user = user;
	}

	public UserDto getUser() {
		return user;
	}

	@Override
	public Type<ActiveUserHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ActiveUserHandler handler) {
		handler.activeUserChanged(this);
	}
}
