/*
 * LoadingStatus.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.client.common;

import com.google.gwt.resources.client.ImageResource;
import com.google.inject.Inject;

public enum LoadingStatus {
	NONE {
		@Override
		ImageResource getIcon() {
			return resources.placeholderIcon();
		}

		@Override
		String getAltText() {
			return null;
		}
	},

	LOADED {
		@Override
		ImageResource getIcon() {
			return resources.loadedIcon();
		}

		@Override
		String getAltText() {
			return messages.loadedAltText();
		}
	},

	NOT_FOUND {
		@Override
		ImageResource getIcon() {
			return resources.notFoundIcon();
		}

		@Override
		String getAltText() {
			return messages.notFoundAltText();
		}
	},

	PENDING {
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
	private static CommonMessages messages;

	abstract ImageResource getIcon();

	abstract String getAltText();
}
