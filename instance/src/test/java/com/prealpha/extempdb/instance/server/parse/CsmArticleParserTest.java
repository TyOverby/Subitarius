/*
 * CsmArticleParserTest.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server.parse;

import static org.junit.Assert.*;

import org.junit.Test;

import com.google.inject.Inject;

public final class CsmArticleParserTest extends ArticleParserTestBase {
	private static final String BASE_URL = "http://www.csmonitor.com/World/Americas/2011/0321/Haiti-elects-new-president-for-Herculean-task";

	private static final String PAGE_URL = "http://www.csmonitor.com/World/Americas/2011/0321/Haiti-elects-new-president-for-Herculean-task/(page)/2";

	@Inject
	private CsmArticleParser parser;

	@Override
	protected ArticleParser getParser() {
		return parser;
	}

	@Test
	public void testPageUrl() {
		assertEquals(BASE_URL, parser.getCanonicalUrl(PAGE_URL));
	}

	@Test
	public void testParse() throws ArticleParseException {
		testVector(0);
	}
	
	@Test
	public void testParse2() throws ArticleParseException {
		testVector(1);
	}

	@Test
	public void testParseList() throws ArticleParseException {
		testVector(2);
	}

	@Test
	public void testParseListItem() throws ArticleParseException {
		testVector(3);
	}
	
	@Test
	public void testParseQuiz() throws ArticleParseException {
		testVector(4);
	}
}
