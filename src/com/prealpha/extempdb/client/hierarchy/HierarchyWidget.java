/*
 * HierarchyWidget.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.client.hierarchy;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellBrowser;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.TreeViewModel;
import com.google.inject.Inject;

/*
 * TODO: see if we can't use listeners to store the state in history tokens
 */
public class HierarchyWidget extends Composite implements
		HierarchyPresenter.Display {
	public static interface HierarchyUiBinder extends
			UiBinder<Widget, HierarchyWidget> {
	}

	@UiField(provided = true)
	final CellBrowser cellBrowser;

	@Inject
	public HierarchyWidget(HierarchyUiBinder uiBinder, TreeViewModel model) {
		cellBrowser = new CellBrowser(model, null);
		initWidget(uiBinder.createAndBindUi(this));
	}
}
