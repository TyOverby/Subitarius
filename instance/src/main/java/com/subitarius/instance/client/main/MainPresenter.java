/*
 * MainPresenter.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.instance.client.main;

import static com.google.common.base.Preconditions.*;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.prealpha.dispatch.shared.DispatcherAsync;
import com.subitarius.action.FetchEntities;
import com.subitarius.action.MutationResult;
import com.subitarius.action.ParseArticles;
import com.subitarius.instance.client.PlacePresenter;
import com.subitarius.instance.client.error.ManagedCallback;

public final class MainPresenter implements PlacePresenter {
	public static interface Display extends IsWidget {
		HasClickHandlers getFetchButton();

		HasClickHandlers getParseButton();
	}

	private final Display display;

	private final DispatcherAsync dispatcher;

	private HandlerRegistration fetchRegistration;

	private HandlerRegistration parseRegistration;

	@Inject
	private MainPresenter(Display display, DispatcherAsync dispatcher) {
		this.display = display;
		this.dispatcher = dispatcher;
	}

	@Override
	public void init() {
		fetchRegistration = display.getFetchButton().addClickHandler(
				new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						dispatcher.execute(FetchEntities.INSTANCE,
								new ReloadCallback());
					}
				});

		parseRegistration = display.getParseButton().addClickHandler(
				new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						dispatcher.execute(ParseArticles.INSTANCE,
								new ReloadCallback());
					}
				});
	}

	@Override
	public Display getDisplay() {
		return display;
	}

	@Override
	public void bind(List<String> parameters) {
		checkArgument(parameters.size() == 0);
	}

	@Override
	public void destroy() {
		fetchRegistration.removeHandler();
		parseRegistration.removeHandler();
	}

	private static final class ReloadCallback extends
			ManagedCallback<MutationResult> {
		@Override
		public void onSuccess(MutationResult result) {
			// TODO: on success, we shouldn't need to reload
			Window.Location.reload();
		}
	}
}
