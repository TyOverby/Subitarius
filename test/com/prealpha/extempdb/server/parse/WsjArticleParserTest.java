/*
 * WsjArticleParserTest.java
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
public class WsjArticleParserTest implements Module {
	private static final String URL = "http://online.wsj.com/article/SB10001424052748704281204575002852055561406.html";

	private static final String URL_ABRIDGED = "http://online.wsj.com/article/SB10001424052748703384204575509630629800258.html";

	private static final String URL_NEWSWIRE = "http://online.wsj.com/article/BT-CO-20100924-703987.html";

	private static final Map<String, String> PARAMETERS = Collections
			.emptyMap();

	@Inject
	@Unit
	private WsjArticleParser articleParser;

	@Mock
	private HttpClient mockHttpClient;

	@Override
	public void configure(Binder binder) {
		binder.install(new ParseModule());
	}

	@Test(expected = NullPointerException.class)
	public void testNull() throws ArticleParseException {
		articleParser.parse(null);
	}

	@Test
	public void testParse() throws ArticleParseException, IOException,
			RobotsExclusionException {
		InputStream stream = new FileInputStream(new File("./test/wsj.html"));
		expect(mockHttpClient.doGet(URL, PARAMETERS)).andReturn(stream);

		doTest();
	}

	@Test
	public void testParseAbridged() throws ArticleParseException, IOException,
			RobotsExclusionException {
		InputStream stream = new FileInputStream(new File(
				"./test/wsj_abridged.html"));
		expect(mockHttpClient.doGet(URL_ABRIDGED, PARAMETERS))
				.andReturn(stream);

		doTestAbridged();
	}

	@Test
	public void testParseNewswire() throws ArticleParseException, IOException,
			RobotsExclusionException {
		InputStream stream = new FileInputStream(new File(
				"./test/wsj_newswire.html"));
		expect(mockHttpClient.doGet(URL_NEWSWIRE, PARAMETERS))
				.andReturn(stream);

		doTestNewswire();
	}

	@Test(expected = ArticleParseException.class)
	public void testHttpFailure() throws ArticleParseException, IOException,
			RobotsExclusionException {
		expect(mockHttpClient.doGet(URL, PARAMETERS)).andThrow(
				new IOException());

		doTest();
	}

	@Test(expected = ArticleParseException.class)
	public void testRobotsExclusion() throws ArticleParseException,
			IOException, RobotsExclusionException {
		expect(mockHttpClient.doGet(URL, PARAMETERS)).andThrow(
				new RobotsExclusionException());

		doTest();
	}

	private void doTest() throws ArticleParseException {
		replay(mockHttpClient);

		ProtoArticle article = articleParser.parse(URL);

		assertNotNull(article);

		assertEquals("11 Minutes of Action", article.getTitle());

		assertEquals("By DAVID BIDERMAN", article.getByline());

		/* The month was originally all caps. */
		Date date = article.getDate();
		assertEquals("January 15, 2010",
				WsjArticleParser.DATE_FORMAT.format(date));

		List<String> paragraphs = article.getParagraphs();
		int paragraphCount = paragraphs.size();
		String firstParagraph = paragraphs.get(0);
		String lastParagraph = paragraphs.get(paragraphCount - 1);

		assertEquals(23, paragraphCount);
		assertTrue(firstParagraph.startsWith("Football fans"));
		assertEquals("Write to David Biderman at David.Biderman@wsj.com",
				lastParagraph);

		verify(mockHttpClient);
	}

	private void doTestAbridged() throws ArticleParseException {
		replay(mockHttpClient);

		ProtoArticle article = articleParser.parse(URL_ABRIDGED);

		assertNotNull(article);

		assertEquals("China Extends Africa Push With Loans, Deal in Ghana",
				article.getTitle());

		assertEquals("BY WILL CONNORS", article.getByline());

		/* The month was originally all caps. */
		Date date = article.getDate();
		assertEquals("September 24, 2010",
				WsjArticleParser.DATE_FORMAT.format(date));

		List<String> paragraphs = article.getParagraphs();
		int paragraphCount = paragraphs.size();
		String firstParagraph = paragraphs.get(0);
		String lastParagraph = paragraphs.get(paragraphCount - 1);

		assertEquals(3, paragraphCount);
		assertTrue(firstParagraph.startsWith("LAGOS, Nigeria—"));
		assertTrue(lastParagraph.endsWith("20 years, ..."));

		verify(mockHttpClient);
	}

	private void doTestNewswire() throws ArticleParseException {
		replay(mockHttpClient);

		ProtoArticle article = articleParser.parse(URL_NEWSWIRE);

		assertNotNull(article);

		assertEquals(
				"Fitch Raises Outlook On Ghana To Stable As Economy Stabilizes",
				article.getTitle());

		assertNull(article.getByline());

		/* The month was originally all caps. Also, there was a time after this. */
		Date date = article.getDate();
		assertEquals("September 24, 2010",
				WsjArticleParser.DATE_FORMAT.format(date));

		List<String> paragraphs = article.getParagraphs();
		int paragraphCount = paragraphs.size();
		String firstParagraph = paragraphs.get(0);
		String lastParagraph = paragraphs.get(paragraphCount - 1);

		assertEquals(4, paragraphCount);
		assertEquals("DOW JONES NEWSWIRES", firstParagraph);
		assertTrue(lastParagraph.endsWith("in June ..."));

		verify(mockHttpClient);
	}
}
