/*
 * ErrorPresenter.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.client.error;

import static com.google.common.base.Preconditions.*;

import java.util.List;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.inject.Inject;
import com.prealpha.extempdb.client.AppPlace;
import com.prealpha.extempdb.client.AppState;
import com.prealpha.extempdb.client.HistoryManager;
import com.prealpha.extempdb.client.PlacePresenter;

public class ErrorPresenter implements PlacePresenter {
	private final ErrorWidget errorWidget;

	private final HistoryManager historyManager;

	private final Scheduler scheduler;

	@Inject
	public ErrorPresenter(ErrorWidget errorWidget,
			HistoryManager historyManager, Scheduler scheduler) {
		this.errorWidget = errorWidget;
		this.historyManager = historyManager;
		this.scheduler = scheduler;

		errorWidget.getBackLink().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				ErrorPresenter.this.historyManager.back();
			}
		});
	}

	@Override
	public void init() {
		Throwable caught = ManagedCallback.getCaught();

		if (caught == null) {
			scheduler.scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					historyManager.setAppState(new AppState(AppPlace.MAIN));
				}
			});
		} else {
			String text = SafeHtmlUtils.htmlEscape(printStackTrace(caught));
			errorWidget.getStackTraceField().setHTML("<pre>" + text + "</pre>");
		}
	}

	@Override
	public ErrorWidget getDisplay() {
		return errorWidget;
	}

	@Override
	public void bind(List<String> parameters) {
		checkArgument(parameters.size() == 0);
	}

	@Override
	public void destroy() {
	}

	private static String printStackTrace(Throwable throwable) {
		String str = throwable.toString() + '\n';

		for (StackTraceElement element : throwable.getStackTrace()) {
			str += "\tat " + element.toString() + '\n';
		}

		Throwable cause = throwable.getCause();
		if (cause != null) {
			str += "Caused by: " + printStackTrace(cause);
		}

		return str;
	}
}
