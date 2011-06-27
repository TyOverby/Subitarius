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
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;

import atunit.AtUnit;
import atunit.Container;
import atunit.Mock;
import atunit.MockFramework;
import atunit.Stub;
import atunit.Unit;

import com.google.inject.Binder;
import com.google.inject.Inject;
import com.google.inject.Module;
import com.prealpha.extempdb.server.domain.Source;
import com.prealpha.extempdb.server.domain.Tag;
import com.prealpha.extempdb.server.http.HttpClient;
import com.prealpha.extempdb.server.http.RobotsExclusionException;

@RunWith(AtUnit.class)
@Container(Container.Option.GUICE)
@MockFramework(MockFramework.Option.EASYMOCK)
public class BingSearchProviderTest implements Module {
	@Inject
	@Unit
	private BingSearchProvider searchProvider;

	@Mock
	private HttpClient mockHttpClient;

	@Stub
	private Tag mockTag;

	@Stub
	private Source mockSource;

	@Override
	public void configure(Binder binder) {
		binder.install(new SearchModule());
	}

	@Test(expected = NullPointerException.class)
	public void testNullQuery() throws SearchUnavailableException {
		searchProvider.search(null);
	}

	@Test
	public void testSearch() throws SearchUnavailableException, IOException,
			RobotsExclusionException {
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
			IOException, RobotsExclusionException {
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
			IOException, RobotsExclusionException {
		String searchUrl = eq(BingSearchProvider.BASE_URL);
		Map<String, String> parameters = anyObject();
		expect(mockHttpClient.doGet(searchUrl, parameters)).andThrow(
				new RobotsExclusionException());

		replay(mockHttpClient);

		SearchQuery query = new SearchQuery(mockSource, mockTag);
		searchProvider.search(query);

		verify(mockHttpClient);
	}
}
