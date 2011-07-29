/*
 * BingSearchProvider.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.central.search;

import static com.google.common.base.Preconditions.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.io.CharStreams;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.prealpha.extempdb.domain.ArticleUrl;
import com.prealpha.extempdb.domain.Source;
import com.prealpha.extempdb.domain.Tag;
import com.prealpha.extempdb.util.http.RobotsExclusionException;
import com.prealpha.extempdb.util.http.SimpleHttpClient;

final class BingSearchProvider implements SearchProvider {
	/*
	 * Package visibility for unit testing.
	 */
	static final String BASE_URL = "http://api.search.live.net/json.aspx";

	private static final String APP_ID = "363DC039EDCF7F7947D73592A171866C93E4D6F6";

	private static final int RESULT_COUNT = 15;

	private final SimpleHttpClient httpClient;

	private final Gson gson;

	@Inject
	public BingSearchProvider(SimpleHttpClient httpClient, Gson gson) {
		this.httpClient = httpClient;
		this.gson = gson;
	}

	@Override
	public List<ArticleUrl> search(Tag tag, Source source)
			throws SearchUnavailableException {
		return search(tag, source, -1);
	}

	@Override
	public List<ArticleUrl> search(Tag tag, Source source, int limit)
			throws SearchUnavailableException {
		checkNotNull(tag);
		checkNotNull(source);

		List<ArticleUrl> articleUrls;
		if (limit == 0) {
			return Collections.<ArticleUrl> emptyList();
		} else if (limit > 0) {
			articleUrls = Lists.newArrayListWithCapacity(limit);
		} else {
			articleUrls = Lists.newArrayList();
		}

		int offset = 0;
		BingSearch search;
		do {
			try {
				search = doRequest(tag, source, offset);
				BingNews news = search.getSearchResponse().getNews();

				if (news == null) {
					break;
				}

				Iterable<BingNewsResult> results = news.getResults();

				if (results == null) {
					break;
				}

				for (BingNewsResult result : results) {
					if (limit < 0 || articleUrls.size() < limit) {
						// handle Bing's apiclick.aspx
						String rawUrl = handleApiClick(result.getUrl());
						ArticleUrl articleUrl = new ArticleUrl(rawUrl, source);
						articleUrls.add(articleUrl);
					}
				}

				offset += RESULT_COUNT;
			} catch (IOException iox) {
				throw new SearchUnavailableException(iox);
			} catch (RobotsExclusionException rex) {
				throw new SearchUnavailableException(rex);
			}
		} while (!isComplete(search)
				&& (limit < 0 || articleUrls.size() < limit));

		return articleUrls;
	}

	private BingSearch doRequest(Tag tag, Source source, int offset)
			throws IOException, RobotsExclusionException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("AppId", APP_ID);
		params.put("Query", getQuery(tag, source));
		params.put("Sources", "News");
		params.put("Version", "2.0");
		params.put("News.Count", Integer.toString(RESULT_COUNT));
		params.put("News.Offset", Integer.toString(offset));
		params.put("News.SortBy", "Relevance");

		InputStream stream = httpClient.doGet(BASE_URL, params);
		InputStreamReader isr = new InputStreamReader(stream);
		String json = CharStreams.toString(isr);
		return gson.fromJson(json, BingSearch.class);
	}

	private static String getQuery(Tag tag, Source source) {
		String query = "site:";
		query += source.getDomainName();
		query += " " + tag.getName();
		return query;
	}

	private static boolean isComplete(BingSearch search) {
		BingNews news = search.getSearchResponse().getNews();
		int total = news.getTotal();
		int offset = news.getOffset();

		return ((offset + RESULT_COUNT) >= total);
	}

	private static String handleApiClick(String url) {
		if (url.startsWith("http://www.bing.com/news/apiclick.aspx")) {
			int urlIndex = url.indexOf("&url=");
			int beginUrlIndex = urlIndex + 5;
			int endUrlIndex = url.indexOf("&", beginUrlIndex);
			url = url.substring(beginUrlIndex, endUrlIndex);

			try {
				return URLDecoder.decode(url, "UTF-8");
			} catch (UnsupportedEncodingException uex) {
				throw new IllegalStateException(uex);
			}
		} else {
			return url;
		}
	}
}
