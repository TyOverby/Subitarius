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
	private static final String ORIGINAL_URL = "http://www.washingtonpost.com/wp-dyn/content/article/2010/09/18/AR2010091800482.html";

	private static final String URL = "http://www.washingtonpost.com/wp-dyn/content/article/2010/09/18/AR2010091800482_pf.html";
	
	private static final String CANONICAL_URL = "http://www.washingtonpost.com/wp-dyn/content/article/2011/01/31/AR2011013103692.html";

	private static final Map<String, String> PARAMETERS = Collections
			.emptyMap();

	@Inject
	@Unit
	private WaPostArticleParser articleParser;

	@Mock
	private HttpClient mockHttpClient;

	@Override
	public void configure(Binder binder) {
		binder.install(new ParseModule());
	}
	
	@Test
	public void testGetCanonicalUrl() {
		String page = articleParser.getCanonicalUrl("http://www.washingtonpost.com/wp-dyn/content/article/2011/01/31/AR2011013103692_2.html");
		assertEquals(CANONICAL_URL, page);
		
		String printable = articleParser.getCanonicalUrl("http://www.washingtonpost.com/wp-dyn/content/article/2011/01/31/AR2011013103692_pf.html");
		assertEquals(CANONICAL_URL, printable);
		
		String parameters = articleParser.getCanonicalUrl("http://www.washingtonpost.com/wp-dyn/content/article/2011/01/31/AR2011013103692.html?nav=hcmodule");
		assertEquals(CANONICAL_URL, parameters);
		
		String pageAndParameters = articleParser.getCanonicalUrl("http://www.washingtonpost.com/wp-dyn/content/article/2011/01/31/AR2011013103692_2.html?nav=hcmodule");
		assertEquals(CANONICAL_URL, pageAndParameters);
	}

	@Test(expected = NullPointerException.class)
	public void testNull() throws ArticleParseException {
		articleParser.parse(null);
	}

	@Test
	public void testParse() throws ArticleParseException, IOException,
			RobotsExclusionException {
		InputStream stream = new FileInputStream(new File("./wapost.html"));
		expect(mockHttpClient.doGet(URL, PARAMETERS)).andReturn(stream);

		doTest();
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

		ProtoArticle article = articleParser.parse(ORIGINAL_URL);

		assertNotNull(article);

		assertEquals(
				"Afghan elections marked by violence, 'irregularities,' modest turnout",
				article.getTitle());

		assertEquals("By David Nakamura and Ernesto Londoño",
				article.getByline());

		Date date = article.getDate();
		assertEquals("Saturday, September 18, 2010",
				WaPostArticleParser.DATE_FORMAT.format(date));

		List<String> paragraphs = article.getParagraphs();
		int paragraphCount = paragraphs.size();
		String firstParagraph = paragraphs.get(0);
		String lastParagraph = paragraphs.get(paragraphCount - 1);

		assertEquals(34, paragraphCount);
		assertTrue(firstParagraph.startsWith("KABUL - There were"));
		assertTrue(lastParagraph
				.endsWith("Javed Hamdard contributed to this report."));

		verify(mockHttpClient);
	}
}
