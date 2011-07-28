/*
 * EmptyCookieStore.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.util.http;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;

class EmptyCookieStore implements CookieStore {
	public EmptyCookieStore() {
	}

	@Override
	public void addCookie(Cookie cookie) {
	}

	@Override
	public void clear() {
	}

	@Override
	public boolean clearExpired(Date date) {
		return false;
	}

	@Override
	public List<Cookie> getCookies() {
		return Collections.emptyList();
	}
}
