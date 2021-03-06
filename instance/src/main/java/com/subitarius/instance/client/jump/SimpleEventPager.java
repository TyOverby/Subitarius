/*
 * SimpleEventPager.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.instance.client.jump;

import com.google.gwt.event.logical.shared.HasShowRangeHandlers;
import com.google.gwt.event.logical.shared.ShowRangeEvent;
import com.google.gwt.event.logical.shared.ShowRangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.cellview.client.SimplePager;

public class SimpleEventPager extends SimplePager implements
		HasShowRangeHandlers<Integer> {
	public SimpleEventPager() {
		super();
	}

	public SimpleEventPager(TextLocation location) {
		super(location);
	}

	public SimpleEventPager(TextLocation location, Resources resources,
			boolean showFastForwardButton, int fastForwardRows,
			boolean showLastPageButton) {
		super(location, resources, showFastForwardButton, fastForwardRows,
				showLastPageButton);
	}

	@Override
	public HandlerRegistration addShowRangeHandler(
			ShowRangeHandler<Integer> handler) {
		return addHandler(handler, ShowRangeEvent.getType());
	}

	@Override
	protected void onRangeOrRowCountChanged() {
		super.onRangeOrRowCountChanged();

		int pageStart = getPageStart();
		int pageEnd = pageStart + getPageSize();
		ShowRangeEvent.fire(this, pageStart, pageEnd);
	}
}
