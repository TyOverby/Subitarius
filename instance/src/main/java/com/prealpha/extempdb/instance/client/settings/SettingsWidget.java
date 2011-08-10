/*
 * SettingsWidget.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.client.settings;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class SettingsWidget extends Composite implements
		SettingsPresenter.Display {
	public static interface ToolsUiBinder extends
			UiBinder<Widget, SettingsWidget> {
	}

	@UiField(provided = true)
	final Widget addArticleWidget;

	@Inject
	public SettingsWidget(ToolsUiBinder uiBinder,
			AddArticlePresenter addArticlePresenter) {
		addArticlePresenter.bind(null);
		addArticleWidget = addArticlePresenter.getDisplay().asWidget();
		initWidget(uiBinder.createAndBindUi(this));
	}
}
