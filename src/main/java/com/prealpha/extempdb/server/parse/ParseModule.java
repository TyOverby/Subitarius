/*
 * ParseModule.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server.parse;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.params.CookieSpecPNames;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.prealpha.simplehttp.SimpleHttpClient;

public final class ParseModule extends AbstractModule {
	private static final String USER_AGENT = "ExtempDB";

	private static final String[] DATE_PATTERNS = new String[] {
			"EEE MMM dd HH:mm:ss yyyy", "EEE, dd-MMM-yyyy HH:mm:ss ZZZ" };

	public ParseModule() {
	}

	@Override
	protected void configure() {
		bind(CookieStore.class).to(EmptyCookieStore.class);
	}

	@Singleton
	@Provides
	@Inject
	HttpClient getBackingClient(DefaultHttpClient backingClient,
			CookieStore cookieStore) {
		backingClient.setCookieStore(cookieStore);
		backingClient.getParams().setParameter(CookieSpecPNames.DATE_PATTERNS,
				Arrays.asList(DATE_PATTERNS));
		return backingClient;
	}

	@Singleton
	@Provides
	@Inject
	SimpleHttpClient getHttpClient(HttpClient httpClient) {
		return new SimpleHttpClient(USER_AGENT, httpClient);
	}

	private static final class EmptyCookieStore implements CookieStore {
		@Override
		public void addCookie(Cookie cookie) {
		}

		@Override
		public List<Cookie> getCookies() {
			return Collections.emptyList();
		}

		@Override
		public boolean clearExpired(Date date) {
			return false;
		}

		@Override
		public void clear() {
		}
	}
}
