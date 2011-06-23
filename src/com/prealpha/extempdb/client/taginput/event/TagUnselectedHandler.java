/*
 * TagUnselectedHandler.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.client.taginput.event;

import com.google.gwt.event.shared.EventHandler;

public interface TagUnselectedHandler extends EventHandler {
	void onTagUnselected(TagUnselectedEvent event);
}
