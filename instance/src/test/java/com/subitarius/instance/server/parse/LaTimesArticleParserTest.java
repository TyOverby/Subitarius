/*
 * LaTimesArticleParserTest.java
 * Copyright (C) 2011 Ty Overby, Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.instance.server.parse;

import org.junit.Test;

import com.google.inject.Inject;

public final class LaTimesArticleParserTest extends ArticleParserTestBase {
	@Inject
	private LaTimesArticleParser parser;

	@Override
	protected ArticleParser getParser() {
		return parser;
	}

	@Test
	public void testParseBlog() throws ArticleParseException {
		testVector(0);
	}

	@Test
	public void testParseFeatured() throws ArticleParseException {
		testVector(1);
	}

	@Test
	public void testParseFeaturedTwo() throws ArticleParseException {
		testVector(2);
	}

	@Test
	public void testParseStandard() throws ArticleParseException {
		testVector(3);
	}

	@Test
	public void testParseOpinion() throws ArticleParseException {
		testVector(4);
	}

	@Test
	public void testParseLakersBlog() throws ArticleParseException {
		testVector(5);
	}

	@Test
	public void testParseEditorial() throws ArticleParseException {
		testVector(6);
	}
}
