/*
 * InstanceServerException.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.launcher;

final class InstanceServerException extends Exception {
	private static final long serialVersionUID = 1L;

	InstanceServerException(Throwable cause) {
		super("exception while starting instance server", cause);
	}
}
