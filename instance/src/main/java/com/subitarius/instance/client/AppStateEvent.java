/*
 * AppStateEvent.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.instance.client;

import static com.google.common.base.Preconditions.*;

import com.google.gwt.event.shared.GwtEvent;

public class AppStateEvent extends GwtEvent<AppStateHandler> {
	private static final Type<AppStateHandler> TYPE = new Type<AppStateHandler>();

	public static Type<AppStateHandler> getType() {
		return TYPE;
	}

	private final AppState appState;

	public AppStateEvent(AppState appState) {
		checkNotNull(appState);
		this.appState = appState;
	}

	public AppState getAppState() {
		return appState;
	}

	@Override
	public Type<AppStateHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(AppStateHandler handler) {
		handler.appStateChanged(this);
	}
}
