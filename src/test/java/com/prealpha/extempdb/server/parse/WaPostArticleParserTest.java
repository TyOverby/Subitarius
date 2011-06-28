/*
 * WaPostArticleParserTest.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server.parse;

import static org.junit.Assert.*;

import org.junit.Test;

import com.google.inject.Inject;

public final class WaPostArticleParserTest extends ArticleParserTestBase {
	private static final String URL_STORY = "http://www.washingtonpost.com/opinions/as-global-crises-mount-obama-has-become-the-worlds-master-of-ceremonies-/2011/03/15/ABAKbLs_story.html";

	private static final String URL_BLOG = "http://www.washingtonpost.com/blogs/blogpost/post/house-gop-rejects-climate-change-amendment-science-not-settled/2011/03/15/ABWUYlY_blog.html";

	@Inject
	private WaPostArticleParser parser;

	@Override
	protected ArticleParser getParser() {
		return parser;
	}

	@Test
	public void testGetCanonicalUrl() {
		// test pages
		for (int i = 1; i <= 20; i++) {
			String pageUrl = "http://www.washingtonpost.com/opinions/as-global-crises-mount-obama-has-become-the-worlds-master-of-ceremonies-/2011/03/15/ABAKbLs_story_"
					+ i + ".html";
			assertEquals(URL_STORY, parser.getCanonicalUrl(pageUrl));
		}

		// test printable version
		String printableUrl = "http://www.washingtonpost.com/opinions/as-global-crises-mount-obama-has-become-the-worlds-master-of-ceremonies-/2011/03/15/ABAKbLs_print.html";
		assertEquals(URL_STORY, parser.getCanonicalUrl(printableUrl));

		// test single page version
		String singlePageUrl = "http://www.washingtonpost.com/opinions/as-global-crises-mount-obama-has-become-the-worlds-master-of-ceremonies-/2011/03/15/ABAKbLs_singlePage.html";
		assertEquals(URL_STORY, parser.getCanonicalUrl(singlePageUrl));

		// make sure that story and blog URLs are canonicalized to themselves
		assertEquals(URL_STORY, parser.getCanonicalUrl(URL_STORY));
		assertEquals(URL_BLOG, parser.getCanonicalUrl(URL_BLOG));
	}

	@Test
	public void testParseStory() throws ArticleParseException {
		testVector(0);
	}

	@Test
	public void testParseBlog() throws ArticleParseException {
		testVector(1);
	}
}
