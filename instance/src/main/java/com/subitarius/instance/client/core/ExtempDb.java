/*
 * ExtempDb.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.instance.client.core;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.subitarius.instance.client.AppPlace;
import com.subitarius.instance.client.error.ManagedCallback;

class ExtempDb {
	public static void onModuleLoad() {
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

	private ExtempDb() {
	}
}
