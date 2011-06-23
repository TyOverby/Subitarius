/*
 * TagUnselectedEvent.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.client.taginput.event;

import com.google.web.bindery.event.shared.Event;

public final class TagUnselectedEvent extends Event<TagUnselectedHandler> {
	private static final Type<TagUnselectedHandler> TYPE = new Type<TagUnselectedHandler>();

	public TagUnselectedEvent() {
	}

	@Override
	public Type<TagUnselectedHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(TagUnselectedHandler handler) {
		handler.onTagUnselected(this);
	}
}
