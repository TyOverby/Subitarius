/*
 * WaPostArticleParserTest.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server.parse;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;

import atunit.AtUnit;
import atunit.Container;
import atunit.Mock;
import atunit.MockFramework;
import atunit.Unit;

import com.google.inject.Binder;
import com.google.inject.Inject;
import com.google.inject.Module;
import com.prealpha.extempdb.server.http.HttpClient;
import com.prealpha.extempdb.server.http.RobotsExclusionException;

@RunWith(AtUnit.class)
@Container(Container.Option.GUICE)
@MockFramework(MockFramework.Option.EASYMOCK)
public class WaPostArticleParserTest implements Module {
	private static final String URL_STORY = "http://www.washingtonpost.com/opinions/as-global-crises-mount-obama-has-become-the-worlds-master-of-ceremonies-/2011/03/15/ABAKbLs_story.html";

	private static final String URL_SINGLEPAGE = "http://www.washingtonpost.com/opinions/as-global-crises-mount-obama-has-become-the-worlds-master-of-ceremonies-/2011/03/15/ABAKbLs_singlePage.html";

	private static final String URL_BLOG = "http://www.washingtonpost.com/blogs/blogpost/post/house-gop-rejects-climate-change-amendment-science-not-settled/2011/03/15/ABWUYlY_blog.html";

	private static final Map<String, String> PARAMETERS = Collections
			.emptyMap();

	@Inject
	@Unit
	private WaPostArticleParser parser;

	@Mock
	private HttpClient mockHttpClient;

	@Override
	public void configure(Binder binder) {
		binder.install(new ParseModule());
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
		assertEquals(URL_STORY, parser.getCanonicalUrl(URL_SINGLEPAGE));

		// make sure that story and blog URLs are canonicalized to themselves
		assertEquals(URL_STORY, parser.getCanonicalUrl(URL_STORY));
		assertEquals(URL_BLOG, parser.getCanonicalUrl(URL_BLOG));
	}

	@Test(expected = NullPointerException.class)
	public void testNull() throws ArticleParseException {
		parser.parse(null);
	}

	@Test(expected = ArticleParseException.class)
	public void testHttpFailure() throws ArticleParseException, IOException,
			RobotsExclusionException {
		expect(mockHttpClient.doGet(URL_SINGLEPAGE, PARAMETERS)).andThrow(
				new IOException());
		replay(mockHttpClient);
		parser.parse(URL_STORY);
	}

	@Test(expected = ArticleParseException.class)
	public void testRobotsExclusion() throws ArticleParseException,
			IOException, RobotsExclusionException {
		expect(mockHttpClient.doGet(URL_SINGLEPAGE, PARAMETERS)).andThrow(
				new RobotsExclusionException());
		replay(mockHttpClient);
		parser.parse(URL_STORY);
	}

	@Test
	public void testParseStory() throws ArticleParseException, IOException,
			RobotsExclusionException {
		InputStream stream = new FileInputStream(
				new File("./wapost_story.html"));
		expect(mockHttpClient.doGet(URL_SINGLEPAGE, PARAMETERS)).andReturn(
				stream);

		replay(mockHttpClient);

		ProtoArticle article = parser.parse(URL_STORY);

		assertNotNull(article);

		assertEquals(
				"As global crises mount, Obama has become the world’s master of ceremonies",
				article.getTitle());

		assertEquals("By David J. Rothkopf", article.getByline());

		Date date = article.getDate();
		assertEquals("2011-03-20", WaPostArticleParser.DATE_FORMAT.format(date));

		List<String> paragraphs = article.getParagraphs();
		int paragraphCount = paragraphs.size();
		String firstParagraph = paragraphs.get(0);
		String lastParagraph = paragraphs.get(paragraphCount - 1);

		assertEquals(17, paragraphCount);
		assertTrue(firstParagraph.startsWith("Crises redefine a presidency"));
		assertTrue(lastParagraph.endsWith("the World They Are Making.”"));
	}

	@Test
	public void testParseBlog() throws ArticleParseException, IOException,
			RobotsExclusionException {
		InputStream stream = new FileInputStream(new File("./wapost_blog.html"));
		expect(mockHttpClient.doGet(URL_BLOG, PARAMETERS)).andReturn(stream);

		replay(mockHttpClient);

		ProtoArticle article = parser.parse(URL_BLOG);

		assertNotNull(article);

		assertEquals(
				"House GOP rejects climate change amendment: Science ‘not settled’",
				article.getTitle());

		assertEquals("By Katie Rogers", article.getByline());

		Date date = article.getDate();
		assertEquals("2011-03-15", WaPostArticleParser.DATE_FORMAT.format(date));

		List<String> paragraphs = article.getParagraphs();
		int paragraphCount = paragraphs.size();
		String firstParagraph = paragraphs.get(0);
		String lastParagraph = paragraphs.get(paragraphCount - 1);

		assertEquals(7, paragraphCount);
		assertTrue(firstParagraph.startsWith("GOP members of the"));
		assertTrue(lastParagraph.endsWith("decide for yourself ."));
	}
}
