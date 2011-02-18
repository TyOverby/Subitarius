/*
 * ManagedCallback.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.client.error;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.prealpha.extempdb.client.AppPlace;
import com.prealpha.extempdb.client.AppState;
import com.prealpha.extempdb.client.HistoryManager;

public abstract class ManagedCallback<T> implements AsyncCallback<T> {
	@Inject
	private static HistoryManager historyManager;

	private static Throwable caught;

	static Throwable getCaught() {
		Throwable caught = ManagedCallback.caught;
		ManagedCallback.caught = null;
		return caught;
	}

	protected ManagedCallback() {
	}

	@Override
	public void onFailure(Throwable caught) {
		ManagedCallback.caught = caught;
		AppState appState = new AppState(AppPlace.ERROR);
		historyManager.setAppState(appState);
	}
}
