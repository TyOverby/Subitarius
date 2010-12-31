/*
 * SessionHandler.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface SessionHandler extends EventHandler {
	void sessionUpdated(SessionEvent event);
}
