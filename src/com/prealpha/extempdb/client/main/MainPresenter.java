/*
 * MainPresenter.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.client.main;

import static com.google.common.base.Preconditions.*;

import java.util.List;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.prealpha.extempdb.client.PlacePresenter;
import com.prealpha.extempdb.client.SessionManager;
import com.prealpha.extempdb.client.event.ActiveUserEvent;
import com.prealpha.extempdb.client.event.ActiveUserHandler;

public class MainPresenter implements PlacePresenter {
	public static interface Display extends IsWidget {
		HasWidgets getPointsPanel();

		HasWidgets getLoginPanel();
	}

	private final Display display;

	private final PointsPresenter pointsPresenter;

	private final LoginPresenter loginPresenter;

	private final SessionManager sessionManager;

	private final EventBus eventBus;

	private HandlerRegistration sessionRegistration;

	@Inject
	public MainPresenter(Display display, PointsPresenter pointsPresenter,
			LoginPresenter loginPresenter, SessionManager sessionManager,
			EventBus eventBus) {
		this.display = display;
		this.pointsPresenter = pointsPresenter;
		this.loginPresenter = loginPresenter;
		this.sessionManager = sessionManager;
		this.eventBus = eventBus;

		Widget pointsWidget = pointsPresenter.getDisplay().asWidget();
		display.getPointsPanel().add(pointsWidget);
		Widget loginWidget = loginPresenter.getDisplay().asWidget();
		display.getLoginPanel().add(loginWidget);
	}

	@Override
	public void init() {
		sessionRegistration = eventBus.addHandler(ActiveUserEvent.getType(),
				new ActiveUserHandler() {
					@Override
					public void activeUserChanged(ActiveUserEvent event) {
						pointsPresenter.bind(event.getUser());
						loginPresenter.bind(event.getUser());
					}
				});
		sessionManager.fireActiveUser();
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
		sessionRegistration.removeHandler();
	}
}
