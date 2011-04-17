/*
 * WsjArticleParserTest.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server.parse;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;

import atunit.AtUnit;
import atunit.Container;
import atunit.Unit;

import com.google.inject.Inject;

@RunWith(AtUnit.class)
@Container(Container.Option.GUICE)
public class WsjArticleParserTest extends ArticleParserTestBase {
	@Inject
	@Unit
	private WsjArticleParser parser;

	@Override
	protected ArticleParser getParser() {
		return parser;
	}

	@Test
	public void testParse() throws ArticleParseException {
		testVector(0);
	}

	@Test
	public void testParseAbridged() throws ArticleParseException {
		testVector(1);
	}

	@Test
	public void testParseLetters() throws ArticleParseException {
		testVector(2);
	}

	@Test
	public void testParseTravelGuide() throws ArticleParseException {
		testVector(3);
	}

	@Test
	public void testParseTemporary() throws ArticleParseException {
		assertNull(parser
				.parse("http://online.wsj.com/article/BT-CO-20110309-703720.html"));
		assertNull(parser
				.parse("http://online.wsj.com/article/PR-CO-20101117-906923.html"));
		assertNull(parser
				.parse("http://online.wsj.com/article/SB128934068314955423.html"));
	}
}
