/*
 * LoadingStatusWidget.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.client.taginput;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
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
	public void setImageResource(ImageResource resource) {
		image.setResource(resource);
	}

	@Override
	public HasText getAltText() {
		return new HasText() {
			@Override
			public String getText() {
				return image.getTitle();
			}

			@Override
			public void setText(String text) {
				image.setTitle(text);
			}
		};
	}
}
