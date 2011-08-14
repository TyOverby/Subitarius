/*
 * UserActionListener.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.launcher;

import java.util.EventListener;

public interface UserActionListener extends EventListener {
	void onActionComplete(UserAction action);
	
	void onActionProgress(UserAction action, int complete, int total);
}
