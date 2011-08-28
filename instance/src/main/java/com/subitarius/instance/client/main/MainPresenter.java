/*
 * MainPresenter.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.instance.client.main;

import static com.google.common.base.Preconditions.*;

import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.subitarius.instance.client.PlacePresenter;

public class MainPresenter implements PlacePresenter {
	public static interface Display extends IsWidget {
	}

	private final Display display;

	@Inject
	public MainPresenter(Display display) {
		this.display = display;
	}

	@Override
	public void init() {
	}

	@Override
	public Display getDisplay() {
		return display;
	}

	@Override
	public void bind(List<String> parameters) {
		checkArgument(parameters.size() == 0);
	}

	@Override
	public void destroy() {
	}
}
