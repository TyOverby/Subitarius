/*
 * AppPlace.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.inject.client.AsyncProvider;
import com.prealpha.extempdb.client.core.CoreManager;

public enum AppPlace {
	ARTICLE {
		@Override
		public AsyncProvider<? extends PlacePresenter> getPresenter() {
			return INJECTOR.getArticlePresenter();
		}
	},

	BROWSE {
		@Override
		public AsyncProvider<? extends PlacePresenter> getPresenter() {
			return INJECTOR.getBrowsePresenter();
		}
	},

	ERROR {
		@Override
		public AsyncProvider<? extends PlacePresenter> getPresenter() {
			return INJECTOR.getErrorPresenter();
		}
	},

	HIERARCHY {
		@Override
		public AsyncProvider<? extends PlacePresenter> getPresenter() {
			return INJECTOR.getHierarchyPresenter();
		}
	},

	MAIN {
		@Override
		public AsyncProvider<? extends PlacePresenter> getPresenter() {
			return INJECTOR.getMainPresenter();
		}
	},

	SETTINGS {
		@Override
		public AsyncProvider<? extends PlacePresenter> getPresenter() {
			return INJECTOR.getSettingsPresenter();
		}
	};

	private static ExtempDbInjector INJECTOR = GWT
			.create(ExtempDbInjector.class);

	public static CoreManager getCoreManager() {
		return INJECTOR.getCoreManager();
	}

	public abstract AsyncProvider<? extends PlacePresenter> getPresenter();
}
