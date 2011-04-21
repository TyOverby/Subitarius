/*
 * HierarchyPresenter.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.client.hierarchy;

import static com.google.common.base.Preconditions.*;

import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.prealpha.extempdb.client.PlacePresenter;

public class HierarchyPresenter implements PlacePresenter {
	public static interface Display extends IsWidget {
	}

	private final Display display;

	@Inject
	public HierarchyPresenter(Display display) {
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
