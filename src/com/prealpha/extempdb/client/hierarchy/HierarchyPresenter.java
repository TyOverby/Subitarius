/*
 * HierarchyPresenter.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.client.hierarchy;

import static com.google.common.base.Preconditions.*;

import java.util.List;

import com.google.inject.Inject;
import com.prealpha.extempdb.client.PlacePresenter;

public class HierarchyPresenter implements PlacePresenter {
	private final HierarchyWidget hierarchyWidget;

	@Inject
	public HierarchyPresenter(HierarchyWidget hierarchyWidget) {
		this.hierarchyWidget = hierarchyWidget;
	}

	@Override
	public void init() {
	}

	@Override
	public HierarchyWidget getDisplay() {
		return hierarchyWidget;
	}

	@Override
	public void bind(List<String> parameters) {
		checkArgument(parameters.size() == 0);
	}

	@Override
	public void destroy() {
	}
}
