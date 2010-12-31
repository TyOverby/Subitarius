/*
 * BrowsePresenter.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.client.browse;

import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.prealpha.extempdb.client.AppPlace;
import com.prealpha.extempdb.client.AppState;
import com.prealpha.extempdb.client.HistoryManager;
import com.prealpha.extempdb.client.PlacePresenter;

public class BrowsePresenter implements PlacePresenter {
	public static interface Display extends IsWidget, HasValue<BrowseState> {
	}

	private final Display display;

	private final HistoryManager historyManager;

	private HandlerRegistration handler;

	@Inject
	public BrowsePresenter(Display display, HistoryManager historyManager) {
		this.display = display;
		this.historyManager = historyManager;
	}

	@Override
	public void init() {
		handler = display
				.addValueChangeHandler(new ValueChangeHandler<BrowseState>() {
					@Override
					public void onValueChange(
							ValueChangeEvent<BrowseState> event) {
						List<String> parameters = event.getValue().serialize();
						AppState appState = new AppState(AppPlace.BROWSE,
								parameters);
						historyManager.setAppState(appState);
					}
				});
	}

	@Override
	public Display getDisplay() {
		return display;
	}

	@Override
	public void bind(List<String> parameters) {
		try {
			BrowseState browseState = BrowseState.deserialize(parameters);
			display.setValue(browseState);
		} catch (NullPointerException npx) {
			throw new IllegalArgumentException(npx);
		}
	}

	@Override
	public void destroy() {
		handler.removeHandler();
	}
}
