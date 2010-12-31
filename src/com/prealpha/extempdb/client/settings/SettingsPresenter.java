/*
 * SettingsPresenter.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.client.settings;

import static com.google.common.base.Preconditions.*;

import java.util.List;

import com.google.inject.Inject;
import com.prealpha.extempdb.client.PlacePresenter;

public class SettingsPresenter implements PlacePresenter {
	private final SettingsWidget widget;

	@Inject
	public SettingsPresenter(SettingsWidget widget) {
		this.widget = widget;
	}

	@Override
	public void init() {
	}

	@Override
	public SettingsWidget getDisplay() {
		return widget;
	}

	@Override
	public void bind(List<String> parameters) {
		checkArgument(parameters.size() == 0);
	}

	@Override
	public void destroy() {
	}
}
