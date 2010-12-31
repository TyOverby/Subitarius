/*
 * CommonResources.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.client.common;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

public interface CommonResources extends ClientBundle {
	Style style();

	@Source("placeholder.png")
	ImageResource placeholderIcon();

	@Source("accept.png")
	ImageResource loadedIcon();

	@Source("cross.png")
	ImageResource notFoundIcon();

	@Source("hourglass.png")
	ImageResource pendingIcon();

	@Source("arrow_up.png")
	ImageResource upArrow();

	@Source("arrow_down.png")
	ImageResource downArrow();

	static interface Style extends CssResource {
		String contentSection();

		String heading();

		String center();

		String loadingStatus();
	}
}
