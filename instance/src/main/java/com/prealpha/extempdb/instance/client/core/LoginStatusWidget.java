/*
 * LoginStatusWidget.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.client.core;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class LoginStatusWidget extends Composite implements
		LoginStatusPresenter.Display {
	public static interface LoginStatusUiBinder extends
			UiBinder<Widget, LoginStatusWidget> {
	}

	@UiField
	HasHTML statusLabel;

	@UiField
	HasClickHandlers logOutLink;

	@Inject
	public LoginStatusWidget(LoginStatusUiBinder uiBinder) {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public HasHTML getStatusLabel() {
		return statusLabel;
	}

	@Override
	public HasClickHandlers getLogOutLink() {
		return logOutLink;
	}
}
