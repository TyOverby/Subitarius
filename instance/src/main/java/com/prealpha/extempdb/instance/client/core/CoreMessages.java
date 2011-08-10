/*
 * CoreMessages.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.client.core;

import com.google.gwt.i18n.client.Messages;

public interface CoreMessages extends Messages {
	String title();

	String jump();

	String hierarchy();

	String loading();

	String settings();

	String logOut();

	String notLoggedIn();

	String loggedIn(String name);
}
