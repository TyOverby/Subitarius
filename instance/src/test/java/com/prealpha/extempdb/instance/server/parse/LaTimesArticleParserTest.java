/*
 * LaTimesArticleParser.java
 * Copyright (C) 2011 Ty Overby
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.server.parse;

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
	public void testBlogPostParse() throws ArticleParseException {
		testVector(0);
	}
}
