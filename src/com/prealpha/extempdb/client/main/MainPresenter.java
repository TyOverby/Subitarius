/*
 * SettingsPresenter.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.client.main;

import static com.google.common.base.Preconditions.*;

import java.util.List;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.prealpha.extempdb.client.PlacePresenter;
import com.prealpha.extempdb.client.SessionManager;
import com.prealpha.extempdb.client.event.SessionEvent;
import com.prealpha.extempdb.client.event.SessionHandler;

public class MainPresenter implements PlacePresenter {
	private final MainWidget mainWidget;

	private final LoginPresenter loginPresenter;

	private final SessionManager sessionManager;

	private final EventBus eventBus;

	private final Scheduler scheduler;

	private HandlerRegistration sessionRegistration;

	@Inject
	public MainPresenter(MainWidget mainWidget, LoginPresenter loginPresenter,
			SessionManager sessionManager, EventBus eventBus,
			Scheduler scheduler) {
		this.mainWidget = mainWidget;
		this.loginPresenter = loginPresenter;
		this.sessionManager = sessionManager;
		this.eventBus = eventBus;
		this.scheduler = scheduler;

		Widget loginWidget = loginPresenter.getDisplay().asWidget();
		mainWidget.getLoginPanel().add(loginWidget);
	}

	@Override
	public void init() {
		sessionRegistration = eventBus.addHandler(SessionEvent.getType(),
				new SessionHandler() {
					@Override
					public void sessionUpdated(SessionEvent event) {
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
	public MainWidget getDisplay() {
		return mainWidget;
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
