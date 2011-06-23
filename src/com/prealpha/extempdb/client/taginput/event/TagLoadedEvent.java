/*
 * TagLoadedEvent.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.client.taginput.event;

import static com.google.common.base.Preconditions.*;

import com.google.web.bindery.event.shared.Event;
import com.prealpha.extempdb.shared.dto.TagDto;

public final class TagLoadedEvent extends Event<TagLoadedHandler> {
	private static final Type<TagLoadedHandler> TYPE = new Type<TagLoadedHandler>();

	private final TagDto tag;

	public TagLoadedEvent(TagDto tag) {
		checkNotNull(tag);
		this.tag = tag;
	}

	public TagDto getTag() {
		return tag;
	}

	@Override
	public Type<TagLoadedHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(TagLoadedHandler handler) {
		handler.onTagLoaded(this);
	}
}
