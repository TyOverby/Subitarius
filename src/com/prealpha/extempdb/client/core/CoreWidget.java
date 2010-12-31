/*
 * CoreWidget.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.client.core;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.prealpha.extempdb.client.event.SessionEvent;
import com.prealpha.extempdb.client.event.SessionHandler;

public class CoreWidget extends Composite {
	public static interface CoreUiBinder extends UiBinder<Widget, CoreWidget> {
	}

	@UiField(provided = true)
	final Widget loginStatus;

	@UiField
	HasWidgets contentPanel;

	@Inject
	public CoreWidget(CoreUiBinder uiBinder,
			final LoginStatusPresenter loginPresenter, EventBus eventBus) {
		eventBus.addHandler(SessionEvent.getType(), new SessionHandler() {
			@Override
			public void sessionUpdated(SessionEvent event) {
				loginPresenter.bind(event.getSession());
			}
		});

		loginStatus = loginPresenter.getDisplay().asWidget();
		initWidget(uiBinder.createAndBindUi(this));
	}

	public HasWidgets getContentPanel() {
		return contentPanel;
	}
}
