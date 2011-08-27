/*
 * InstanceServerException.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.launcher;

final class InstanceServerException extends Exception {
	InstanceServerException(Throwable cause) {
		super("exception while starting instance server", cause);
	}
}
