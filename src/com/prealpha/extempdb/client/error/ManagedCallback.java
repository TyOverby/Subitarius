/*
 * ManagedCallback.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.client.error;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;

public abstract class ManagedCallback<T> implements AsyncCallback<T> {
	private static Throwable caught;

	static Throwable getCaught() {
		Throwable caught = ManagedCallback.caught;
		ManagedCallback.caught = null;
		return caught;
	}

	protected ManagedCallback() {
	}

	@Override
	public void onFailure(Throwable caught) {
		ManagedCallback.caught = caught;
		History.newItem("ERROR"); // TODO: not structured
	}
}
