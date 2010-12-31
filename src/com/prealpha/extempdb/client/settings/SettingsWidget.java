/*
 * SettingsWidget.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.client.settings;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class SettingsWidget extends Composite {
	public static interface ToolsUiBinder extends
			UiBinder<Widget, SettingsWidget> {
	}

	@UiField(provided = true)
	final Widget passwordChangeWidget;

	@UiField(provided = true)
	final Widget tagManagerWidget;

	/*
	 * TODO: some way to do permission control. Not a real security hole though.
	 * Maybe just make the presenters bind on UserSession?
	 */
	@Inject
	public SettingsWidget(ToolsUiBinder uiBinder,
			PasswordChangePresenter passwordChangePresenter,
			TagManagerPresenter tagManagerPresenter) {
		passwordChangePresenter.bind(null);
		tagManagerPresenter.bind(null);
		passwordChangeWidget = passwordChangePresenter.getDisplay().asWidget();
		tagManagerWidget = tagManagerPresenter.getDisplay().asWidget();

		initWidget(uiBinder.createAndBindUi(this));
	}
}
