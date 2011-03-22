/*
 * EconomistArticleParserTest.java
 * Copyright (C) 2011 Meyer Kizner, Ty Overby
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
public class EconomistArticleParserTest implements Module {
	private static final String URL_PRINT = "http://www.economist.com/node/18388998";

	private static final String URL_BLOG = "http://www.economist.com/blogs/newsbook/2011/03/weeks_caption_competition_1";

	private static final Map<String, String> PARAMETERS = Collections
			.emptyMap();

	@Inject
	@Unit
	private EconomistArticleParser parser;

	@Mock
	private HttpClient mockHttpClient;

	@Override
	public void configure(Binder binder) {
		binder.install(new ParseModule());
	}

	@Test(expected = NullPointerException.class)
	public void testNull() throws ArticleParseException {
		parser.parse(null);
	}

	@Test(expected = ArticleParseException.class)
	public void testHttpFailure() throws ArticleParseException, IOException,
			RobotsExclusionException {
		expect(mockHttpClient.doGet(URL_PRINT + "/print", PARAMETERS))
				.andThrow(new IOException());
		replay(mockHttpClient);
		parser.parse(URL_PRINT);
	}

	@Test(expected = ArticleParseException.class)
	public void testRobotsExclusion() throws ArticleParseException,
			IOException, RobotsExclusionException {
		expect(mockHttpClient.doGet(URL_PRINT + "/print", PARAMETERS))
				.andThrow(new RobotsExclusionException());
		replay(mockHttpClient);
		parser.parse(URL_PRINT);
	}

	@Test
	public void testParsePrint() throws ArticleParseException, IOException,
			RobotsExclusionException {
		InputStream stream = new FileInputStream(new File(
				"./test/economist_print.html"));
		expect(mockHttpClient.doGet(URL_PRINT + "/print", PARAMETERS))
				.andReturn(stream);

		replay(mockHttpClient);

		ProtoArticle article = parser.parse(URL_PRINT);

		assertNotNull(article);

		assertEquals(
				"Video nasty: The film business is slumping. It needs to start dealing directly with consumers",
				article.getTitle());

		assertNull(article.getByline());

		// the "th" in "17th" is stripped off by the parser
		Date date = article.getDate();
		assertEquals("Mar 17 2011",
				EconomistArticleParser.DATE_FORMAT.format(date));

		List<String> paragraphs = article.getParagraphs();
		int paragraphCount = paragraphs.size();
		String firstParagraph = paragraphs.get(0);
		String lastParagraph = paragraphs.get(paragraphCount - 1);

		assertEquals(7, paragraphCount);
		assertTrue(firstParagraph.startsWith("IN “THE RING”,"));
		assertTrue(lastParagraph.endsWith("at least, it should."));
	}

	@Test
	public void testParseBlog() throws ArticleParseException, IOException,
			RobotsExclusionException {
		InputStream stream = new FileInputStream(new File(
				"./test/economist_blog.html"));
		expect(mockHttpClient.doGet(URL_BLOG + "/print", PARAMETERS))
				.andReturn(stream);

		replay(mockHttpClient);

		ProtoArticle article = parser.parse(URL_BLOG);

		assertNotNull(article);

		assertEquals("Caption competition 8: This week's caption competition",
				article.getTitle());

		assertEquals("by The Economist online", article.getByline());

		// the "st" in "21st" is stripped off by the parser
		Date date = article.getDate();
		assertEquals("Mar 21 2011",
				EconomistArticleParser.DATE_FORMAT.format(date));

		List<String> paragraphs = article.getParagraphs();
		int paragraphCount = paragraphs.size();
		String firstParagraph = paragraphs.get(0);
		String lastParagraph = paragraphs.get(paragraphCount - 1);

		assertEquals(2, paragraphCount);
		assertTrue(firstParagraph.startsWith("The photograph above"));
		assertTrue(lastParagraph.endsWith("Over to you."));
	}
}
