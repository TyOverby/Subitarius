/*
 * ReutersArticleParserTest.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server.parse;

import org.junit.Test;
import org.junit.runner.RunWith;

import atunit.AtUnit;
import atunit.Container;
import atunit.Unit;

import com.google.inject.Inject;

@RunWith(AtUnit.class)
@Container(Container.Option.GUICE)
public class ReutersArticleParserTest extends ArticleParserTestBase {
	@Inject
	@Unit
	private ReutersArticleParser parser;

	@Override
	protected ArticleParser getParser() {
		return parser;
	}

	@Test
	public void testParse() throws ArticleParseException {
		testVector(0);
	}

	@Test
	public void testParseSlideshow() throws ArticleParseException {
		testVector(1);
	}
}
