/*
 * MainWidget.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.client.main;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class MainWidget extends Composite implements MainPresenter.Display {
	public static interface MainUiBinder extends UiBinder<Widget, MainWidget> {
	}

	@UiField
	HasWidgets pointsPanel;

	@UiField
	HasWidgets loginPanel;

	@Inject
	public MainWidget(MainUiBinder uiBinder) {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public HasWidgets getPointsPanel() {
		return pointsPanel;
	}

	@Override
	public HasWidgets getLoginPanel() {
		return loginPanel;
	}
}
