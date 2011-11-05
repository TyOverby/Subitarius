/*
 * RecordTest.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.util.http.robots;

import static org.junit.Assert.*;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.junit.Before;
import org.junit.Test;

public final class RecordTest {
	private Record excludeAll;

	private Record allowAll;

	private Record allowAllWithComment;

	private Record excludePart;

	private Record excludeSingle;
	
	private Record commentOnly;

	@Before
	public void setUp() {
		excludeAll = new Record("User-agent: *\nDisallow: /\n");
		allowAll = new Record("User-agent: *\nDisallow: \n");
		allowAllWithComment = new Record(
				"# Some comment\nUser-agent: *\nDisallow: \n");
		excludePart = new Record(
				"User-agent: *\nDisallow: /cgi-bin/\nDisallow: /tmp/\nDisallow: /junk/\n");
		excludeSingle = new Record("User-agent: BadBot\nDisallow: /\n");
		commentOnly = new Record("# Some comment\n");
	}

	@Test
	public void testRequestRoot() {
		assertFalse(excludeAll.apply(getRequest("/")));
		assertTrue(allowAll.apply(getRequest("/")));
		assertTrue(allowAllWithComment.apply(getRequest("/")));
		assertTrue(excludePart.apply(getRequest("/")));
		assertTrue(excludeSingle.apply(getRequest("/")));
		assertTrue(commentOnly.apply(getRequest("/")));
	}

	@Test
	public void testRequestPart() {
		assertFalse(excludeAll.apply(getRequest("/junk/page.html")));
		assertTrue(allowAll.apply(getRequest("/junk/page.html")));
		assertTrue(allowAllWithComment.apply(getRequest("/junk/page.html")));
		assertFalse(excludePart.apply(getRequest("/junk/page.html")));
		assertTrue(excludeSingle.apply(getRequest("/junk/page.html")));
		assertTrue(commentOnly.apply(getRequest("/junk/page.html")));
	}

	@Test
	public void testRequestSimilar() {
		assertFalse(excludeAll.apply(getRequest("/junk")));
		assertTrue(allowAll.apply(getRequest("/junk")));
		assertTrue(allowAllWithComment.apply(getRequest("/junk")));
		assertTrue(excludePart.apply(getRequest("/junk")));
		assertTrue(excludeSingle.apply(getRequest("/junk")));
		assertTrue(commentOnly.apply(getRequest("/junk")));
	}

	@Test
	public void testRequestBad() {
		assertFalse(excludeAll.apply(getRequest("/", "BadBot")));
		assertTrue(allowAll.apply(getRequest("/", "BadBot")));
		assertTrue(allowAllWithComment.apply(getRequest("/", "BadBot")));
		assertTrue(excludePart.apply(getRequest("/", "BadBot")));
		assertFalse(excludeSingle.apply(getRequest("/", "BadBot")));
		assertTrue(commentOnly.apply(getRequest("/", "BadBot")));
	}

	private static HttpUriRequest getRequest(String path) {
		return getRequest(path, "GoodBot");
	}

	private static HttpUriRequest getRequest(String path, String userAgent) {
		HttpUriRequest request = new HttpGet(path);
		request.setHeader("User-Agent", userAgent);
		return request;
	}
}
