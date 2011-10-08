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
	public void testParseOpinion() throws ArticleParseException {
		testVector(1);
	}

	@Test
	public void testParseRoomForDebate() throws ArticleParseException {
		testVector(2);
	}

	@Test
	public void testParseRoomForDebateIntro() throws ArticleParseException {
		testVector(3);
	}

	@Test
	public void testParseSchoolbook() throws ArticleParseException {
		testVector(4);
	}

	@Test
	public void testParseMultimedia() throws ArticleParseException {
		testVector(5);
	}

	@Test
	public void testParseReference() throws ArticleParseException {
		testVector(6);
	}
}
