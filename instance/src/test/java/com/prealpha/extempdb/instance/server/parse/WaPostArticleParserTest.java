/*
 * WaPostArticleParserTest.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.server.parse;

import org.junit.Test;

import com.google.inject.Inject;

public final class WaPostArticleParserTest extends ArticleParserTestBase {
	@Inject
	private WaPostArticleParser parser;

	@Override
	protected ArticleParser getParser() {
		return parser;
	}

	@Test
	public void testParseStory() throws ArticleParseException {
		testVector(0);
	}

	@Test
	public void testParseBlog() throws ArticleParseException {
		testVector(1);
	}
}
