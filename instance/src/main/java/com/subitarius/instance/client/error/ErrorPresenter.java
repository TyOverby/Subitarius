/*
 * ErrorPresenter.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.instance.client.error;

import static com.google.common.base.Preconditions.*;

import java.util.List;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.rpc.IncompatibleRemoteServiceException;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.subitarius.instance.client.AppPlace;
import com.subitarius.instance.client.AppState;
import com.subitarius.instance.client.HistoryManager;
import com.subitarius.instance.client.PlacePresenter;

public class ErrorPresenter implements PlacePresenter {
	public static interface Display extends IsWidget {
		HasText getMessageLabel();

		HasHTML getStackTraceField();

		HasClickHandlers getBackLink();
	}

	private final Display display;

	private final HistoryManager historyManager;

	private final Scheduler scheduler;

	private final ErrorMessages messages;

	@Inject
	public ErrorPresenter(Display display, HistoryManager historyManager,
			Scheduler scheduler, ErrorMessages messages) {
		this.display = display;
		this.historyManager = historyManager;
		this.scheduler = scheduler;
		this.messages = messages;

		display.getBackLink().addClickHandler(new ClickHandler() {
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
			display.getMessageLabel().setText(null);
			scheduler.scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					historyManager.setAppState(new AppState(AppPlace.MAIN));
				}
			});
		} else if (caught instanceof IncompatibleRemoteServiceException) {
			display.getMessageLabel().setText(messages.incompatibleService());
		} else {
			display.getMessageLabel().setText(messages.exception());
			String stackTrace = SafeHtmlUtils
					.htmlEscape(printStackTrace(caught));
			display.getStackTraceField().setHTML(
					"<pre>" + stackTrace + "</pre>");
		}
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
