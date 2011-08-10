/*
 * PointsWidget.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.client.main;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class PointsWidget extends Composite implements PointsPresenter.Display {
	public static interface PointsUiBinder extends
			UiBinder<Widget, PointsWidget> {
	}

	@UiField(provided = true)
	final Grid pointsGrid;

	@Inject
	public PointsWidget(PointsUiBinder uiBinder) {
		pointsGrid = new Grid(0, 2);
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void add(String userName, int points) {
		int rowIndex = pointsGrid.getRowCount();
		pointsGrid.resizeRows(rowIndex + 1);
		pointsGrid.getCellFormatter().setWidth(rowIndex, 0, "80px");
		pointsGrid.setText(rowIndex, 0, userName);
		pointsGrid.setText(rowIndex, 1, Integer.toString(points));
	}

	@Override
	public void clear() {
		pointsGrid.resizeRows(0);
	}
}
