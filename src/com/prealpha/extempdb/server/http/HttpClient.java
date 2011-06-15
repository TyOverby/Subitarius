/*
 * HttpClient.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;

import com.google.common.io.CharStreams;
import com.google.inject.Inject;
import com.prealpha.extempdb.server.InjectLogger;
import com.prealpha.extempdb.server.http.robots.RobotsTxt;

/*
 * NOTE: this class is not thread-safe
 */
public class HttpClient {
	private static final String USER_AGENT = "ExtempDB";

	@InjectLogger
	private Logger log;

	private final org.apache.http.client.HttpClient httpClient;

	private final Map<String, RobotsTxt> robotsExclusion;

	@Inject
	public HttpClient(org.apache.http.client.HttpClient httpClient) {
		this.httpClient = httpClient;
		robotsExclusion = new HashMap<String, RobotsTxt>();
	}

	public InputStream doGet(String url, Map<String, String> parameters)
			throws IOException, RobotsExclusionException {
		// Washington Post gives us 400 if we request robots.txt with the ?
		String uri;
		if (!parameters.isEmpty()) {
			List<NameValuePair> params = getNameValuePairs(parameters);
			String encodedParams = URLEncodedUtils.format(params, "UTF-8");
			uri = url + '?' + encodedParams;
		} else {
			uri = url;
		}
		HttpGet get = new HttpGet(uri);
		return execute(get);
	}

	public InputStream doPost(String url, Map<String, String> parameters)
			throws IOException, RobotsExclusionException {
		HttpPost post = new HttpPost(url);
		List<NameValuePair> params = getNameValuePairs(parameters);
		UrlEncodedFormEntity inputEntity = new UrlEncodedFormEntity(params,
				"UTF-8");
		post.setEntity(inputEntity);
		return execute(post);
	}

	private InputStream execute(HttpUriRequest request) throws IOException,
			RobotsExclusionException {
		request.setHeader("User-Agent", USER_AGENT);

		URI uri = request.getURI();
		String authority = uri.getAuthority();
		if (!robotsExclusion.containsKey(authority)) {
			robotsExclusion.put(authority, null); // to allow this request
			String url = uri.getScheme() + "://" + authority + "/robots.txt";
			Map<String, String> parameters = Collections.emptyMap();

			try {
				InputStream stream = doGet(url, parameters);
				InputStreamReader isr = new InputStreamReader(stream);
				RobotsTxt robotsTxt = new RobotsTxt(CharStreams.toString(isr));
				robotsExclusion.put(authority, robotsTxt);
			} catch (IOException iox) {
				// it's set to null in the map already, so we move on
			}
		}

		RobotsTxt robotsTxt = robotsExclusion.get(authority);
		if (robotsTxt != null && !robotsTxt.apply(request)) {
			log.debug("rejected request for URI {} due to robots.txt", uri);
			throw new RobotsExclusionException(uri.toString(), robotsTxt);
		}

		HttpResponse response = httpClient.execute(request);
		HttpEntity outputEntity = response.getEntity();
		int statusCode = response.getStatusLine().getStatusCode();

		log.debug("URI {} returned HTTP status code {}", uri, statusCode);

		if (statusCode != HttpStatus.SC_OK) {
			if (outputEntity != null) {
				EntityUtils.consume(outputEntity);
			}

			throw new StatusCodeException(statusCode);
		}

		if (outputEntity != null) {
			return outputEntity.getContent();
		} else {
			throw new IOException("no response body");
		}
	}

	private static List<NameValuePair> getNameValuePairs(
			Map<String, String> parameters) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();

		for (Map.Entry<String, String> entry : parameters.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			NameValuePair pair = new BasicNameValuePair(key, value);
			params.add(pair);
		}

		return params;
	}
}
