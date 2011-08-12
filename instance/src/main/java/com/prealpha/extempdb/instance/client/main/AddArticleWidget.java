/*
 * AddArticleWidget.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.client.main;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class AddArticleWidget extends Composite implements
		AddArticlePresenter.Display {
	public static interface AddArticleUiBinder extends
			UiBinder<Widget, AddArticleWidget> {
	}

	@UiField
	HasText statusLabel;

	@UiField
	HasText urlBox;

	@UiField
	HasClickHandlers addButton;

	@Inject
	public AddArticleWidget(AddArticleUiBinder uiBinder) {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public HasText getStatusLabel() {
		return statusLabel;
	}

	@Override
	public HasText getUrlBox() {
		return urlBox;
	}

	@Override
	public HasClickHandlers getAddButton() {
		return addButton;
	}
}
