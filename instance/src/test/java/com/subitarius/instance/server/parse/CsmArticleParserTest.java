/*
 * CsmArticleParserTest.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.instance.server.parse;

import org.junit.Test;

import com.google.inject.Inject;

public final class CsmArticleParserTest extends ArticleParserTestBase {
	@Inject
	private CsmArticleParser parser;

	@Override
	protected ArticleParser getParser() {
		return parser;
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
