/*
 * CsmArticleParserTest.java
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
public class CsmArticleParserTest implements Module {
	private static final String URL = "http://www.csmonitor.com/USA/Election-2010/Vox-News/2010/0907/Restoring-Truthiness-Could-spoof-of-Glenn-Beck-rally-happen";

	private static final Map<String, String> PARAMETERS = Collections
			.emptyMap();

	@Inject
	@Unit
	private CsmArticleParser articleParser;

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
		InputStream stream = new FileInputStream(new File("./csm.html"));
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

		ProtoArticle article = articleParser.parse(URL);

		assertNotNull(article);

		assertEquals(
				"'Restoring Truthiness': Could spoof of Glenn Beck rally happen?",
				article.getTitle());

		assertEquals("By Gloria Goodale", article.getByline());

		Date date = article.getDate();
		assertEquals("September 7, 2010",
				CsmArticleParser.DATE_FORMAT.format(date));

		List<String> paragraphs = article.getParagraphs();
		int paragraphCount = paragraphs.size();
		String firstParagraph = paragraphs.get(0);
		String lastParagraph = paragraphs.get(paragraphCount - 1);

		assertEquals(11, paragraphCount);
		assertTrue(firstParagraph.startsWith("Can the Internet"));
		assertTrue(lastParagraph.endsWith("a real politician.”"));

		verify(mockHttpClient);
	}
}
