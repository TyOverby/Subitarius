/*
 * ActiveUserHandler.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface ActiveUserHandler extends EventHandler {
	void activeUserChanged(ActiveUserEvent event);
}
