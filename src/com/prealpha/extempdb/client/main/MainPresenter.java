/*
 * MainPresenter.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.client.main;

import static com.google.common.base.Preconditions.*;

import java.util.List;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.prealpha.extempdb.client.PlacePresenter;
import com.prealpha.extempdb.client.SessionManager;
import com.prealpha.extempdb.client.event.SessionEvent;
import com.prealpha.extempdb.client.event.SessionHandler;

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

	private final Scheduler scheduler;

	private HandlerRegistration sessionRegistration;

	@Inject
	public MainPresenter(Display display, PointsPresenter pointsPresenter,
			LoginPresenter loginPresenter, SessionManager sessionManager,
			EventBus eventBus, Scheduler scheduler) {
		this.display = display;
		this.pointsPresenter = pointsPresenter;
		this.loginPresenter = loginPresenter;
		this.sessionManager = sessionManager;
		this.eventBus = eventBus;
		this.scheduler = scheduler;

		Widget pointsWidget = pointsPresenter.getDisplay().asWidget();
		display.getPointsPanel().add(pointsWidget);
		Widget loginWidget = loginPresenter.getDisplay().asWidget();
		display.getLoginPanel().add(loginWidget);
	}

	@Override
	public void init() {
		sessionRegistration = eventBus.addHandler(SessionEvent.getType(),
				new SessionHandler() {
					@Override
					public void sessionUpdated(SessionEvent event) {
						pointsPresenter.bind(event.getSession());
						loginPresenter.bind(event.getSession());
					}
				});

		scheduler.scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				sessionManager.fireCurrentSession();
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
		sessionRegistration.removeHandler();
	}
}
