/*
 * SettingsPresenter.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.client.settings;

import static com.google.common.base.Preconditions.*;

import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.prealpha.extempdb.instance.client.PlacePresenter;

public class SettingsPresenter implements PlacePresenter {
	public static interface Display extends IsWidget {
	}

	private final Display display;

	@Inject
	public SettingsPresenter(Display display) {
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
