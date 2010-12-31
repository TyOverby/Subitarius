/*
 * ErrorWidget.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.client.error;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class ErrorWidget extends Composite {
	public static interface ErrorUiBinder extends UiBinder<Widget, ErrorWidget> {
	}

	@UiField
	HasHTML stackTraceField;

	@UiField
	HasClickHandlers backLink;

	@Inject
	public ErrorWidget(ErrorUiBinder uiBinder) {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public HasHTML getStackTraceField() {
		return stackTraceField;
	}

	public HasClickHandlers getBackLink() {
		return backLink;
	}
}
