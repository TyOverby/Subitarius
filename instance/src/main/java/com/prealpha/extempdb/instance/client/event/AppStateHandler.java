/*
 * AppStateHandler.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface AppStateHandler extends EventHandler {
	void appStateChanged(AppStateEvent event);
}
