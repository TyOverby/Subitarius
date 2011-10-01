/*
 * RobotsTxtTest.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.util.http.robots;

import static org.junit.Assert.*;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.junit.Before;
import org.junit.Test;

public final class RobotsTxtTest {
	private RobotsTxt noRestriction;

	private RobotsTxt sitemapOnly;

	private RobotsTxt someRestricted;

	private RobotsTxt allRestricted;

	@Before
	public void setUp() {
		noRestriction = new RobotsTxt("User-agent: *\nDisallow:\n");
		sitemapOnly = new RobotsTxt(
				"User-agent: *\nSitemap: http://www.somesite.com/sitemap.xml\n");
		someRestricted = new RobotsTxt("User-agent: *\nDisallow: /someFile\n");
		allRestricted = new RobotsTxt("User-agent: *\nDisallow: /\n");
	}

	@Test
	public void testApplyNoRestriction() {
		assertTrue(noRestriction.apply(getRequest("/")));
		assertTrue(noRestriction.apply(getRequest("/someFile")));
	}

	@Test
	public void testSitemapOnly() {
		assertTrue(sitemapOnly.apply(getRequest("/")));
		assertTrue(sitemapOnly.apply(getRequest("/someFile")));
	}

	@Test
	public void testSomeRestricted() {
		assertTrue(someRestricted.apply(getRequest("/")));
		assertFalse(someRestricted.apply(getRequest("/someFile")));
	}

	@Test
	public void testApplyAllRestricted() {
		assertFalse(allRestricted.apply(getRequest("/")));
		assertFalse(allRestricted.apply(getRequest("/someFile")));
	}

	private static HttpUriRequest getRequest(String path) {
		HttpUriRequest request = new HttpGet(path);
		request.setHeader("User-Agent", "TestAgent");
		return request;
	}
}
