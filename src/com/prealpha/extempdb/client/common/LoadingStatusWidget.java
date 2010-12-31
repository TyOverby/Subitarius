/*
 * LoadingStatusWidget.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.client.common;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class LoadingStatusWidget extends Composite implements
		LoadingStatusPresenter.Display {
	public static interface LoadingStatusUiBinder extends
			UiBinder<Widget, LoadingStatusWidget> {
	}

	@UiField
	Image image;

	@Inject
	public LoadingStatusWidget(LoadingStatusUiBinder uiBinder) {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public Image getImage() {
		return image;
	}
}
