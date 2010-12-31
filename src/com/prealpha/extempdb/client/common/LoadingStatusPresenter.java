/*
 * LoadingStatusPresenter.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.client.common;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.prealpha.extempdb.client.Presenter;

public class LoadingStatusPresenter implements Presenter<LoadingStatus> {
	public static interface Display extends IsWidget {
		Image getImage();
	}

	private final Display display;

	@Inject
	public LoadingStatusPresenter(Display display) {
		this.display = display;
	}

	@Override
	public Display getDisplay() {
		return display;
	}

	@Override
	public void bind(LoadingStatus loadingStatus) {
		if (loadingStatus == null) {
			loadingStatus = LoadingStatus.NONE;
		}

		ImageResource resource = loadingStatus.getIcon();
		display.getImage().setResource(resource);
		display.getImage().setTitle(loadingStatus.getAltText());
	}
}
