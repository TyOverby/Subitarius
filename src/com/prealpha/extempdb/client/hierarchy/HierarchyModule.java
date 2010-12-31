/*
 * HierarchyModule.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.client.hierarchy;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.gwt.view.client.TreeViewModel;

public class HierarchyModule extends AbstractGinModule {
	public HierarchyModule() {
	}

	@Override
	protected void configure() {
		bind(HierarchyPresenter.Display.class).to(HierarchyWidget.class);
		bind(TreeViewModel.class).to(TagTreeViewModel.class);
		bind(SelectionModel.class).to(SingleSelectionModel.class);
	}
}
