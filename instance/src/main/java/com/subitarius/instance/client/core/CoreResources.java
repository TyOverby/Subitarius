/*
 * CoreResources.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.instance.client.core;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;

public interface CoreResources extends ClientBundle {
	@Source("core.css")
	Style style();

	static interface Style extends CssResource {
		String container();

		String titleBar();

		String title();

		String buttonContainer();

		String navButton();

		String contentPanel();
	}
}
