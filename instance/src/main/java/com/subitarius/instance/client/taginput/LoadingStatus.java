/*
 * LoadingStatus.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.instance.client.taginput;

import com.google.gwt.resources.client.ImageResource;
import com.google.inject.Inject;
import com.subitarius.instance.client.CommonResources;

enum LoadingStatus {
	NONE(false) {
		@Override
		ImageResource getIcon() {
			return resources.placeholderIcon();
		}

		@Override
		String getAltText() {
			return null;
		}
	},

	LOADED(true) {
		@Override
		ImageResource getIcon() {
			return resources.checkMarkIcon();
		}

		@Override
		String getAltText() {
			return messages.loadedAltText();
		}
	},

	NOT_FOUND(true) {
		@Override
		ImageResource getIcon() {
			return resources.crossIcon();
		}

		@Override
		String getAltText() {
			return messages.notFoundAltText();
		}
	},

	PENDING(false) {
		@Override
		ImageResource getIcon() {
			return resources.pendingIcon();
		}

		@Override
		String getAltText() {
			return messages.pendingAltText();
		}
	};

	@Inject
	private static CommonResources resources;

	@Inject
	private static TagInputMessages messages;

	private final boolean loaded;

	private LoadingStatus(boolean loaded) {
		this.loaded = loaded;
	}

	public boolean isLoaded() {
		return loaded;
	}

	abstract ImageResource getIcon();

	abstract String getAltText();
}
