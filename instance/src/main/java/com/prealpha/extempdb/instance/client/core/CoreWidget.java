/*
 * CoreWidget.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.client.core;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class CoreWidget extends Composite {
	public static interface CoreUiBinder extends UiBinder<Widget, CoreWidget> {
	}

	@UiField
	HasWidgets contentPanel;

	@Inject
	public CoreWidget(CoreUiBinder uiBinder) {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public HasWidgets getContentPanel() {
		return contentPanel;
	}
}
