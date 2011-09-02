/*
 * LaTimesArticleParser.java
 * Copyright (C) 2011 Ty Overby
 * All rights reserved.
 */

package com.subitarius.instance.server.parse;

import org.junit.Test;

import com.google.inject.Inject;

public class LaTimesArticleParserTest extends ArticleParserTestBase {
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
	public void testParseStandard() throws ArticleParseException {
		testVector(2);
	}
}
