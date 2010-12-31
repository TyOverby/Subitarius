/*
 * LoginWidget.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.client.main;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class LoginWidget extends Composite implements LoginPresenter.Display {
	public static interface LoginUiBinder extends UiBinder<Widget, LoginWidget> {
	}

	@UiField
	UIObject container;

	@UiField
	HasText nameBox;

	@UiField
	HasText passwordBox;

	@UiField
	Button submitButton;

	@UiField
	HasText statusLabel;

	@Inject
	public LoginWidget(LoginUiBinder uiBinder) {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public UIObject getContainer() {
		return container;
	}

	@Override
	public HasText getNameBox() {
		return nameBox;
	}

	@Override
	public HasText getPasswordBox() {
		return passwordBox;
	}

	@Override
	public HasClickHandlers getSubmitButton() {
		return submitButton;
	}

	@Override
	public HasText getStatusLabel() {
		return statusLabel;
	}

	@UiHandler({ "nameBox", "passwordBox" })
	void keyPressed(KeyPressEvent event) {
		if (event.getCharCode() == KeyCodes.KEY_ENTER) {
			submitButton.click();
		}
	}
}
