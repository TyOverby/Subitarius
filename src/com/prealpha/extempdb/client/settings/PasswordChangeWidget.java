/*
 * PasswordChangeWidget.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.client.settings;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class PasswordChangeWidget extends Composite implements
		PasswordChangePresenter.Display {
	public static interface PasswordChangeUiBinder extends
			UiBinder<Widget, PasswordChangeWidget> {
	}

	@UiField
	HasText statusLabel;

	@UiField
	HasText currentPasswordBox;

	@UiField
	HasText newPasswordBox;

	@UiField
	HasText confirmPasswordBox;

	@UiField
	HasClickHandlers submitButton;

	@Inject
	public PasswordChangeWidget(PasswordChangeUiBinder uiBinder) {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public HasText getStatusLabel() {
		return statusLabel;
	}

	@Override
	public HasText getCurrentPasswordBox() {
		return currentPasswordBox;
	}

	@Override
	public HasText getNewPasswordBox() {
		return newPasswordBox;
	}

	@Override
	public HasText getConfirmPasswordBox() {
		return confirmPasswordBox;
	}

	@Override
	public HasClickHandlers getSubmitButton() {
		return submitButton;
	}
}
