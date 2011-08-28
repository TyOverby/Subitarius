/*
 * BingSearchProviderTest.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.central.search;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.easymock.Mock;
import com.mycila.testing.plugin.guice.Bind;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.subitarius.domain.ArticleUrl;
import com.subitarius.domain.Source;
import com.subitarius.domain.Tag;
import com.subitarius.domain.Tag.Type;
import com.subitarius.util.http.HttpModule;
import com.subitarius.util.http.RobotsExclusionException;
import com.subitarius.util.http.SimpleHttpClient;
import com.subitarius.util.logging.TestLoggingModule;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ HttpModule.class, SearchModule.class, TestLoggingModule.class })
public final class BingSearchProviderTest {
	@Inject
	private BingSearchProvider searchProvider;

	@Mock(Mock.Type.STRICT)
	@Bind
	private SimpleHttpClient mockHttpClient;

	private Tag tag;

	private Source source;

	@Before
	public void setUp() {
		tag = new Tag("Network neutrality", Type.SEARCHED,
				Collections.<Tag> emptySet());
		source = Source.NY_TIMES;
	}

	@Test(expected = NullPointerException.class)
	public void testNullTag() throws SearchUnavailableException {
		searchProvider.search(null, source);
	}

	@Test(expected = NullPointerException.class)
	public void testNullSource() throws SearchUnavailableException {
		searchProvider.search(tag, null);
	}

	@Test
	public void testSearch() throws SearchUnavailableException, IOException,
			RobotsExclusionException {
		InputStream stream = getClass().getResourceAsStream("bing.json");
		String searchUrl = eq(BingSearchProvider.BASE_URL);
		Map<String, String> parameters = anyObject();
		expect(mockHttpClient.doGet(searchUrl, parameters)).andReturn(stream);

		ArticleUrl expectedUrl = new ArticleUrl(
				"http://www.nytimes.com/2010/08/05/technology/05secret.html");

		replay(mockHttpClient);

		List<ArticleUrl> articleUrls = searchProvider.search(tag, source);
		assertNotNull(articleUrls);
		assertEquals(1, articleUrls.size());

		ArticleUrl actualUrl = articleUrls.iterator().next();
		assertEquals(expectedUrl, actualUrl);

		verify(mockHttpClient);
	}

	@Test(expected = SearchUnavailableException.class)
	public void testHttpFailure() throws SearchUnavailableException,
			IOException, RobotsExclusionException {
		String searchUrl = eq(BingSearchProvider.BASE_URL);
		Map<String, String> parameters = anyObject();
		expect(mockHttpClient.doGet(searchUrl, parameters)).andThrow(
				new IOException());

		replay(mockHttpClient);

		searchProvider.search(tag, source);

		verify(mockHttpClient);
	}

	@Test(expected = SearchUnavailableException.class)
	public void testRobotsExclusion() throws SearchUnavailableException,
			IOException, RobotsExclusionException {
		String searchUrl = eq(BingSearchProvider.BASE_URL);
		Map<String, String> parameters = anyObject();
		expect(mockHttpClient.doGet(searchUrl, parameters)).andThrow(
				new RobotsExclusionException());

		replay(mockHttpClient);

		searchProvider.search(tag, source);

		verify(mockHttpClient);
	}
}
