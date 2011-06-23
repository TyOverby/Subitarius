/*
 * TagLoadedHandler.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.client.taginput.event;

import com.google.gwt.event.shared.EventHandler;

public interface TagLoadedHandler extends EventHandler {
	void onTagLoaded(TagLoadedEvent event);
}
