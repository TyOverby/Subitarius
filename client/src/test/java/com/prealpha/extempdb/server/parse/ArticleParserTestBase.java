/*
 * ArticleParserTestBase.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server.parse;

import static org.junit.Assert.*;

import java.beans.XMLDecoder;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.prealpha.extempdb.util.http.HttpModule;
import com.prealpha.extempdb.util.logging.TestLoggingModule;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ HttpModule.class, TestLoggingModule.class })
public abstract class ArticleParserTestBase {
	private static final String URL = "http://www.nytimes.com/interactive/2010/11/13/weekinreview/deficits-graphic.html";

	private final List<Map.Entry<String, ProtoArticle>> testVectors;

	protected ArticleParserTestBase() {
		InputStream stream = getClass().getResourceAsStream(
				getClass().getSimpleName() + ".xml");
		XMLDecoder decoder = new XMLDecoder(stream);
		@SuppressWarnings("unchecked")
		Map<String, ProtoArticle> map = (Map<String, ProtoArticle>) decoder
				.readObject();
		testVectors = new ArrayList<Map.Entry<String, ProtoArticle>>(
				map.entrySet());
	}

	protected abstract ArticleParser getParser();

	protected void testVector(int index) throws ArticleParseException {
		Map.Entry<String, ProtoArticle> vector = testVectors.get(index);
		String url = vector.getKey();
		ProtoArticle expected = vector.getValue();
		ProtoArticle actual = getParser().parse(url);

		// if expected is null, we have to return out now to avoid NPE
		if (expected == null) {
			assertNull(actual);
			return;
		}

		// check each part individually so that we can see failures easily
		assertEquals("title", expected.getTitle(), actual.getTitle());
		assertEquals("byline", expected.getByline(), actual.getByline());
		assertEquals("date", expected.getDate(), actual.getDate());

		// check each paragraph individually as well
		List<String> expectedText = expected.getParagraphs();
		List<String> actualText = actual.getParagraphs();
		assertEquals("paragraph count", expectedText.size(), actualText.size());
		for (int i = 0; i < expectedText.size(); i++) {
			assertEquals("paragraph " + i, expectedText.get(i),
					actualText.get(i));
		}
		assertEquals("paragraphs", expectedText, actualText);

		// just in case
		assertEquals("all", expected, actual);
	}

	@Test
	public void testNoArgumentUrl() {
		String canonical = getParser().getCanonicalUrl(URL);
		assertEquals(URL, canonical);
	}

	@Test
	public void testArgumentUrl() {
		String canonical = getParser().getCanonicalUrl(URL + "?hp");
		assertEquals(URL, canonical);
	}

	@Test
	public void testMultiArgumentUrl() {
		String canonical = getParser().getCanonicalUrl(
				URL + "?_r=1&pagewanted=all");
		assertEquals(URL, canonical);
	}
}
