/*
 * BbcArticleParserTest.java
 * Copyright (C) 2011 Ty Overby
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.server.parse;

import org.junit.Test;

import com.google.inject.Inject;

public final class BbcArticleParserTest extends ArticleParserTestBase {
	@Inject
	private BbcArticleParser parser;

	@Override
	protected ArticleParser getParser() {
		return this.parser;
	}

	@Test
	public void testParse() throws ArticleParseException {
		this.testVector(0);
	}
}
