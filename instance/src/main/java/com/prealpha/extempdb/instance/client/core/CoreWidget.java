/*
 * CoreWidget.java
 * Copyright (C) 2011 Meyer Kizner
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
import com.prealpha.extempdb.client.event.ActiveUserEvent;
import com.prealpha.extempdb.client.event.ActiveUserHandler;

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
		eventBus.addHandler(ActiveUserEvent.getType(), new ActiveUserHandler() {
			@Override
			public void activeUserChanged(ActiveUserEvent event) {
				loginPresenter.bind(event.getUser());
			}
		});

		loginStatus = loginPresenter.getDisplay().asWidget();
		initWidget(uiBinder.createAndBindUi(this));
	}

	public HasWidgets getContentPanel() {
		return contentPanel;
	}
}
