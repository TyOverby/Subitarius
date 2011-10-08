/*
 * NyTimesArticleParserTest.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.instance.server.parse;

import org.junit.Test;

import com.google.inject.Inject;

public final class NyTimesArticleParserTest extends ArticleParserTestBase {
	@Inject
	private NyTimesArticleParser parser;

	@Override
	protected ArticleParser getParser() {
		return parser;
	}

	@Test
	public void testParseArticle() throws ArticleParseException {
		testVector(0);
	}

	@Test
	public void testParseSchoolbook() throws ArticleParseException {
		testVector(1);
	}
}
