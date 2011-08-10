/*
 * MainWidget.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.client.main;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class MainWidget extends Composite implements MainPresenter.Display {
	public static interface MainUiBinder extends UiBinder<Widget, MainWidget> {
	}

	@Inject
	public MainWidget(MainUiBinder uiBinder) {
		initWidget(uiBinder.createAndBindUi(this));
	}
}
