/*
 * ActionAppender.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.launcher;

import java.net.URL;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;

import com.google.common.collect.Sets;

final class ActionAppender extends AppenderSkeleton {
	private static final Set<Action> ACTIONS = Sets.newHashSet();

	static Collection<Action> getActions() {
		return ACTIONS;
	}

	private ActionAppender() {
	}

	@Override
	public void close() {
	}

	@Override
	public boolean requiresLayout() {
		return false;
	}

	@Override
	protected void append(final LoggingEvent event) {
		ACTIONS.add(new Action() {
			@Override
			public Type getType() {
				Level level = event.getLevel();
				if (level.equals(Level.ERROR)) {
					return Type.ERROR;
				} else if (level.equals(Level.WARN)) {
					return Type.WARN;
				} else {
					return Type.INFO;
				}
			}

			@Override
			public Date getTimestamp() {
				return new Date(event.getTimeStamp());
			}

			@Override
			public URL getUrl() {
				return null;
			}

			@Override
			public String toString() {
				return event.getRenderedMessage();
			}
		});
	}
}
