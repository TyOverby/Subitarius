/*
 * AppPlace.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.instance.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.inject.client.AsyncProvider;
import com.subitarius.instance.client.core.CoreManager;

public enum AppPlace {
	ARTICLE {
		@Override
		public AsyncProvider<? extends PlacePresenter> getPresenter() {
			return INJECTOR.getArticlePresenter();
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

	JUMP {
		@Override
		public AsyncProvider<? extends PlacePresenter> getPresenter() {
			return INJECTOR.getJumpPresenter();
		}
	},

	MAIN {
		@Override
		public AsyncProvider<? extends PlacePresenter> getPresenter() {
			return INJECTOR.getMainPresenter();
		}
	};

	private static SubitariusInjector INJECTOR = GWT
			.create(SubitariusInjector.class);

	public static CoreManager getCoreManager() {
		return INJECTOR.getCoreManager();
	}

	public abstract AsyncProvider<? extends PlacePresenter> getPresenter();
}
