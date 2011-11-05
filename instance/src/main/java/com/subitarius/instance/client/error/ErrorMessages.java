/*
 * ErrorMessages.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.instance.client.error;

import com.google.gwt.i18n.client.Messages;

public interface ErrorMessages extends Messages {
	String heading();

	String exception();

	String serverException();

	String incompatibleService();

	String back();
}
