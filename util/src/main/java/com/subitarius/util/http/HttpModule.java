/*
 * HttpModule.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.util.http;

import java.util.Arrays;
import java.util.Map;

import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.cookie.params.CookieSpecPNames;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.common.collect.MapMaker;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.subitarius.util.http.robots.RobotsTxt;

public class HttpModule extends AbstractModule {
	private static final String[] DATE_PATTERNS = new String[] {
			"EEE MMM dd HH:mm:ss yyyy", "EEE, dd-MMM-yyyy HH:mm:ss ZZZ" };

	public HttpModule() {
	}

	@Override
	protected void configure() {
		bind(SimpleHttpClient.class);
		bind(CookieStore.class).to(EmptyCookieStore.class);
	}

	@Provides
	@Inject
	HttpClient getBackingClient(DefaultHttpClient backingClient,
			CookieStore cookieStore) {
		backingClient.setCookieStore(cookieStore);
		backingClient.getParams().setParameter(CookieSpecPNames.DATE_PATTERNS,
				Arrays.asList(DATE_PATTERNS));
		return backingClient;
	}

	@RobotsExclusion
	@Singleton
	@Provides
	Map<String, RobotsTxt> getRobotsExclusion() {
		return new MapMaker().makeMap();
	}
}
