/*
 * JumpPresenter.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.client.jump;

import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.prealpha.extempdb.instance.client.AppPlace;
import com.prealpha.extempdb.instance.client.AppState;
import com.prealpha.extempdb.instance.client.HistoryManager;
import com.prealpha.extempdb.instance.client.PlacePresenter;

public class JumpPresenter implements PlacePresenter {
	public static interface Display extends IsWidget, HasValue<JumpState> {
	}

	private final Display display;

	private final HistoryManager historyManager;

	private HandlerRegistration handler;

	@Inject
	public JumpPresenter(Display display, HistoryManager historyManager) {
		this.display = display;
		this.historyManager = historyManager;
	}

	@Override
	public void init() {
		handler = display
				.addValueChangeHandler(new ValueChangeHandler<JumpState>() {
					@Override
					public void onValueChange(
							ValueChangeEvent<JumpState> event) {
						List<String> parameters = event.getValue().serialize();
						AppState appState = new AppState(AppPlace.JUMP,
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
			JumpState jumpState = JumpState.deserialize(parameters);
			display.setValue(jumpState);
		} catch (NullPointerException npx) {
			throw new IllegalArgumentException(npx);
		}
	}

	@Override
	public void destroy() {
		handler.removeHandler();
	}
}
