/*
 * GuardianArticleParserTest.java
 * Copyright (C) 2011 Meyer Kizner, Ty Overby
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.server.parse;

import org.junit.Test;

import com.google.inject.Inject;

public final class GuardianArticleParserTest extends ArticleParserTestBase {
	@Inject
	private GuardianArticleParser parser;

	@Override
	protected ArticleParser getParser() {
		return parser;
	}

	@Test
	public void testParse() throws ArticleParseException {
		testVector(0);
	}

	@Test
	public void testParseAp() throws ArticleParseException {
		testVector(1);
	}

	@Test
	public void testParseReuters() throws ArticleParseException {
		testVector(2);
	}
	
	@Test
	public void testParsePa() throws ArticleParseException {
		testVector(3);
	}

	@Test
	public void testParseBlog() throws ArticleParseException {
		testVector(4);
	}

	@Test
	public void testParseContest() throws ArticleParseException {
		testVector(5);
	}
}
