/*
 * ReutersArticleParserTest.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.server.parse;

import org.junit.Test;

import com.google.inject.Inject;

public final class ReutersArticleParserTest extends ArticleParserTestBase {
	@Inject
	private ReutersArticleParser parser;

	@Override
	protected ArticleParser getParser() {
		return parser;
	}

	@Test
	public void testParse() throws ArticleParseException {
		testVector(0);
	}

	@Test
	public void testParseSlideshow() throws ArticleParseException {
		testVector(1);
	}
}
