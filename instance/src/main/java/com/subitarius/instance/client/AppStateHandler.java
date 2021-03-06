/*
 * AppStateHandler.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.instance.client;

import com.google.gwt.event.shared.EventHandler;

public interface AppStateHandler extends EventHandler {
	void appStateChanged(AppStateEvent event);
}
