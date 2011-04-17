/*
 * GuardianArticleParserTest.java
 * Copyright (C) 2011 Meyer Kizner, Ty Overby
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
public class GuardianArticleParserTest extends ArticleParserTestBase {
	@Inject
	@Unit
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
	public void testParseFeed() throws ArticleParseException {
		testVector(1);
	}
}
