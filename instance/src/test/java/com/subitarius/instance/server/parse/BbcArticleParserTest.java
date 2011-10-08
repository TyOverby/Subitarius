/*
 * BbcArticleParserTest.java
 * Copyright (C) 2011 Ty Overby, Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.instance.server.parse;

import org.junit.Test;

import com.google.inject.Inject;

public final class BbcArticleParserTest extends ArticleParserTestBase {
	@Inject
	private BbcArticleParser parser;

	@Override
	protected ArticleParser getParser() {
		return parser;
	}

	@Test
	public void testParse() throws ArticleParseException {
		testVector(0);
	}

	@Test
	public void testParseVideo() throws ArticleParseException {
		testVector(1);
	}

	@Test
	public void testParseProgramme() throws ArticleParseException {
		testVector(2);
	}

	@Test
	public void testParseSports() throws ArticleParseException {
		testVector(3);
	}

	@Test
	public void testParseNewsbeat() throws ArticleParseException {
		testVector(4);
	}
}
