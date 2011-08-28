/*
 * MainWidget.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.instance.client.main;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class MainWidget extends Composite implements MainPresenter.Display {
	public static interface MainUiBinder extends UiBinder<Widget, MainWidget> {
	}

	@UiField(provided = true)
	final Widget addArticleWidget;

	@Inject
	public MainWidget(MainUiBinder uiBinder,
			AddArticlePresenter addArticlePresenter) {
		addArticleWidget = addArticlePresenter.getDisplay().asWidget();
		initWidget(uiBinder.createAndBindUi(this));
	}
}
