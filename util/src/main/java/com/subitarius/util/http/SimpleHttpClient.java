/*
 * SimpleHttpClient.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.util.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.CharStreams;
import com.google.inject.Inject;
import com.subitarius.util.http.robots.RobotsTxt;
import com.subitarius.util.logging.InjectLogger;

/*
 * TODO: it should probably be possible to set the user agent
 * This class is non-final so that it can be mocked out.
 */
public class SimpleHttpClient {
	private static final String USER_AGENT = "Subitarius";

	@InjectLogger
	private Logger log;

	private final HttpClient httpClient;

	private final Map<String, RobotsTxt> robotsExclusion;

	@Inject
	private SimpleHttpClient(HttpClient httpClient,
			@RobotsExclusion Map<String, RobotsTxt> robotsExclusion) {
		this.httpClient = httpClient;
		this.robotsExclusion = robotsExclusion;
	}

	public InputStream doGet(String url) throws IOException,
			RobotsExclusionException {
		return doGet(url, ImmutableMap.<String, String> of());
	}

	public InputStream doGet(String url, Map<String, String> parameters)
			throws IOException, RobotsExclusionException {
		// Washington Post gives us 400 if we request robots.txt with the ?
		String fullUrl;
		if (!parameters.isEmpty()) {
			List<NameValuePair> params = getNameValuePairs(parameters);
			String encodedParams = URLEncodedUtils.format(params, "UTF-8");
			fullUrl = url + '?' + encodedParams;
		} else {
			fullUrl = url;
		}
		HttpGet get = new HttpGet(fullUrl);
		return execute(get);
	}

	public InputStream doPost(String url) throws IOException,
			RobotsExclusionException {
		return doPost(url, ImmutableMap.<String, String> of());
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
		URI uri = request.getURI();
		String authority = uri.getAuthority();
		log.trace("{} {}", authority, request.getRequestLine());
		request.setHeader("User-Agent", USER_AGENT);

		// download and store the robots.txt if not already present
		if (!uri.getPath().equals("/robots.txt")
				&& !robotsExclusion.containsKey(authority)) {
			String url = uri.getScheme() + "://" + authority + "/robots.txt";
			try {
				InputStream stream = doGet(url);
				InputStreamReader isr = new InputStreamReader(stream);
				RobotsTxt robotsTxt = new RobotsTxt(CharStreams.toString(isr));
				robotsExclusion.put(authority, robotsTxt);
				log.debug("found robots.txt for authority {}", authority);
			} catch (IOException iox) {
				// add a blank robots.txt to the map
				robotsExclusion.put(authority, new RobotsTxt());
				log.debug("no robots.txt found for authority {}", authority);
			}
		}

		// check robots.txt before making the request
		RobotsTxt robotsTxt = robotsExclusion.get(authority);
		if (robotsTxt != null && !robotsTxt.apply(request)) {
			throw new RobotsExclusionException(uri.toString(), robotsTxt);
		}

		// do the request
		HttpResponse response = httpClient.execute(request);
		HttpEntity outputEntity = response.getEntity();
		log.trace("{} {}", authority, response.getStatusLine());

		// throw an exception if the status code wasn't 200
		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode != HttpStatus.SC_OK) {
			if (outputEntity != null) {
				EntityUtils.consume(outputEntity);
			}
			throw new StatusCodeException(statusCode);
		}

		// return the response body
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
