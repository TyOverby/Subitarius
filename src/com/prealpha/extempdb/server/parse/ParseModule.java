/*
 * ParseModule.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server.parse;

import org.w3c.tidy.Tidy;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class ParseModule extends AbstractModule {
	@Override
	protected void configure() {
	}

	@Provides
	Tidy getTidy() {
		Tidy tidy = new Tidy();
		tidy.setShowErrors(0);
		tidy.setShowWarnings(false);
		tidy.setQuiet(true);
		tidy.setInputEncoding("UTF-8");
		return tidy;
	}
}
