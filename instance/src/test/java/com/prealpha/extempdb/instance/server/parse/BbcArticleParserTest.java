package com.prealpha.extempdb.instance.server.parse;

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
}
