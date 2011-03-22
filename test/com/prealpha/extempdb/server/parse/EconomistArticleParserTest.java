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
import java.net.URL;
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
	private static URL url;

	private static final Map<String, String> PARAMETERS = Collections
			.emptyMap();

	@Inject
	@Unit
	private EconomistArticleParser articleParser;

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
		//print edition
		url = new URL("http://www.economist.com/node/18388998/print");
		//blogspam
		url = new URL("http://www.economist.com/blogs/newsbook/2011/03/weeks_caption_competition_1/print");

		expect(mockHttpClient.doGet(url.toString(), PARAMETERS)).andReturn(url.openStream());

		doTest();
	}

	@Test(expected = ArticleParseException.class)
	public void testHttpFailure() throws ArticleParseException, IOException,
			RobotsExclusionException {
		expect(mockHttpClient.doGet(url.toString(), PARAMETERS)).andThrow(
				new IOException());

		doTest();
	}

	@Test(expected = ArticleParseException.class)
	public void testRobotsExclusion() throws ArticleParseException,
			IOException, RobotsExclusionException {
		expect(mockHttpClient.doGet(url.toString(), PARAMETERS)).andThrow(
				new RobotsExclusionException());

		doTest();
	}

	private void doTest() throws ArticleParseException {
		replay(mockHttpClient);

		ProtoArticle article = articleParser.parse(url.toString());
		System.out.println(article.toString());

		assertNotNull(article);

		assertEquals("Nuclear power? No thanks (again)", article.getTitle());

		assertEquals("B.U.", article.getByline());

		Date date = article.getDate();

		/*
		 * assertEquals("Thursday 27 January 2011",
		 * EconomistArticleParser.DATE_FORMAT_UK.format(date));
		 */
		List<String> paragraphs = article.getParagraphs();
		int paragraphCount = paragraphs.size();
		String firstParagraph = paragraphs.get(0);
		String lastParagraph = paragraphs.get(paragraphCount - 1);
		assertEquals(7, paragraphCount);
		assertTrue(firstParagraph.startsWith("THIS post will attempt to avoid earthquake"));
		assertTrue(lastParagraph.endsWith("it will be gas."));
		verify(mockHttpClient);

	}
}
