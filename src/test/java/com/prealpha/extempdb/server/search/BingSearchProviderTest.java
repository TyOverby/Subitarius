/*
 * BingSearchProviderTest.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server.search;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.easymock.Mock;
import com.mycila.testing.plugin.guice.Bind;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.prealpha.extempdb.server.LoggingModule;
import com.prealpha.extempdb.server.domain.Source;
import com.prealpha.extempdb.server.domain.Tag;
import com.prealpha.simplehttp.SimpleHttpClient;
import com.prealpha.simplehttp.SimpleHttpException;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ LoggingModule.class, SearchModule.class })
public final class BingSearchProviderTest {
	@Inject
	private BingSearchProvider searchProvider;

	@Mock(Mock.Type.STRICT)
	@Bind
	private SimpleHttpClient mockHttpClient;

	@Mock(Mock.Type.STRICT)
	private Tag mockTag;

	@Mock(Mock.Type.STRICT)
	private Source mockSource;

	@Test(expected = NullPointerException.class)
	public void testNullQuery() throws SearchUnavailableException {
		searchProvider.search(null);
	}

	@Test
	public void testSearch() throws SearchUnavailableException, IOException,
			SimpleHttpException {
		expect(mockTag.getName()).andReturn("network neutrality");
		expect(mockSource.getDomainName()).andReturn("www.nytimes.com");

		InputStream stream = getClass().getResourceAsStream("bing.json");
		String searchUrl = eq(BingSearchProvider.BASE_URL);
		Map<String, String> parameters = anyObject();
		expect(mockHttpClient.doGet(searchUrl, parameters)).andReturn(stream);

		String articleUrl = "http://www.nytimes.com/2010/08/05/technology/05secret.html";

		replay(mockHttpClient, mockTag, mockSource);

		SearchQuery query = new SearchQuery(mockSource, mockTag);
		List<String> urls = searchProvider.search(query);
		assertNotNull(urls);
		assertEquals(1, urls.size());

		String url = urls.iterator().next();
		assertEquals(articleUrl, url);

		verify(mockHttpClient, mockTag, mockSource);
	}

	@Test(expected = SearchUnavailableException.class)
	public void testHttpFailure() throws SearchUnavailableException,
			IOException, SimpleHttpException {
		String searchUrl = eq(BingSearchProvider.BASE_URL);
		Map<String, String> parameters = anyObject();
		expect(mockHttpClient.doGet(searchUrl, parameters)).andThrow(
				new IOException());

		replay(mockHttpClient);

		SearchQuery query = new SearchQuery(mockSource, mockTag);
		searchProvider.search(query);

		verify(mockHttpClient);
	}

	@Test(expected = SearchUnavailableException.class)
	public void testRobotsExclusion() throws SearchUnavailableException,
			IOException, SimpleHttpException, URISyntaxException {
		String searchUrl = eq(BingSearchProvider.BASE_URL);
		Map<String, String> parameters = anyObject();
		expect(mockHttpClient.doGet(searchUrl, parameters)).andThrow(
				new SimpleHttpException(new URI(BingSearchProvider.BASE_URL)));

		replay(mockHttpClient);

		SearchQuery query = new SearchQuery(mockSource, mockTag);
		searchProvider.search(query);

		verify(mockHttpClient);
	}
}
