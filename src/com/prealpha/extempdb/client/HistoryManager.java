/*
 * HistoryManager.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.client;

import static com.google.common.base.Preconditions.*;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.History;
import com.google.inject.Inject;
import com.prealpha.extempdb.client.event.AppStateEvent;

public final class HistoryManager {
	@Inject
	public HistoryManager(final EventBus eventBus) {
		History.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				AppState appState;

				try {
					appState = new AppState(event.getValue());
				} catch (AppStateException asx) {
					setAppState(new AppState(AppPlace.MAIN));
					return;
				}

				eventBus.fireEventFromSource(new AppStateEvent(appState), this);
			}
		});
	}

	public AppState getAppState() {
		try {
			return new AppState(History.getToken());
		} catch (AppStateException asx) {
			// should never happen
			assert false;
			throw new IllegalStateException();
		}
	}

	public void setAppState(AppState appState) {
		checkNotNull(appState);
		String token = appState.toString();
		History.newItem(token);
	}

	public void fireCurrentState() {
		History.fireCurrentHistoryState();
	}

	public void back() {
		History.back();
	}
}
