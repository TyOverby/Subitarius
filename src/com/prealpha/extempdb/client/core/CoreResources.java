/*
 * CoreResources.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.client.core;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;

public interface CoreResources extends ClientBundle {
	Style style();

	static interface Style extends CssResource {
		String container();

		String titleBar();

		String title();

		String buttonContainer();

		String navButton();

		String loginStatus();

		String contentPanel();
	}
}
