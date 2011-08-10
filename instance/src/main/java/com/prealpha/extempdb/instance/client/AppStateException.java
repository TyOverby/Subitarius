/*
 * AppStateException.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.client;

import static com.google.common.base.Preconditions.*;

/**
 * Thrown to indicate that the current {@link AppState} is invalid in some way.
 * When exceptions of this type are caught, the {@code AppState} should be reset
 * to a known default.
 * 
 * @author Meyer Kizner
 * 
 */
public class AppStateException extends Exception {
	private final AppState appState;

	public AppStateException() {
		super();
		appState = null;
	}

	public AppStateException(String message) {
		super(message);
		appState = null;
	}

	public AppStateException(AppState appState) {
		this("invalid AppState: " + appState, appState);
	}

	public AppStateException(String message, AppState appState) {
		super(message);
		checkNotNull(appState);
		this.appState = appState;
	}

	public AppState getAppState() {
		return appState;
	}
}
