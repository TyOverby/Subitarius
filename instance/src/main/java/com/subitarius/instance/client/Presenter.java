/*
 * Presenter.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.instance.client;

import com.google.gwt.user.client.ui.IsWidget;

public interface Presenter<T> {
	IsWidget getDisplay();

	void bind(T t);
}
