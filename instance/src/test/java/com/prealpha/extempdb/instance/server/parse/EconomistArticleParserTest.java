/*
 * EconomistArticleParserTest.java
 * Copyright (C) 2011 Meyer Kizner, Ty Overby
 * All rights reserved.
 */

package com.prealpha.extempdb.server.parse;

import org.junit.Test;

import com.google.inject.Inject;

public final class EconomistArticleParserTest extends ArticleParserTestBase {
	@Inject
	private EconomistArticleParser parser;

	@Override
	protected ArticleParser getParser() {
		return parser;
	}

	@Test
	public void testParsePrint() throws ArticleParseException {
		testVector(0);
	}

	@Test
	public void testParseBlog() throws ArticleParseException {
		testVector(1);
	}

	@Test
	public void testParseBlog2() throws ArticleParseException {
		testVector(2);
	}
}
