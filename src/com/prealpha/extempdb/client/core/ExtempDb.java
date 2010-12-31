/*
 * ExtempDb.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.client.core;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.prealpha.extempdb.client.AppPlace;
import com.prealpha.extempdb.client.error.ManagedCallback;

class ExtempDb implements EntryPoint {
	@Override
	public void onModuleLoad() {
		GWT.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			@Override
			public void onUncaughtException(Throwable caught) {
				new ManagedCallback<Object>() {
					@Override
					public void onSuccess(Object result) {
						assert false;
					}
				}.onFailure(caught);
			}
		});

		AppPlace.getCoreManager().init();
	}
}
