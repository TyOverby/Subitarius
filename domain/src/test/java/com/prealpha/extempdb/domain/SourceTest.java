/*
 * SourceTest.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.domain;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public final class SourceTest {
	private static final String COMMON_URL = "http://www.nytimes.com/interactive/2010/11/13/weekinreview/deficits-graphic.html";

	private static final String WP_STORY = "http://www.washingtonpost.com/opinions/as-global-crises-mount-obama-has-become-the-worlds-master-of-ceremonies-/2011/03/15/ABAKbLs_story.html";

	private static final String WP_BLOG = "http://www.washingtonpost.com/blogs/blogpost/post/house-gop-rejects-climate-change-amendment-science-not-settled/2011/03/15/ABWUYlY_blog.html";

	private static final String CSM_BASE = "http://www.csmonitor.com/World/Americas/2011/0321/Haiti-elects-new-president-for-Herculean-task";

	private static final String CSM_PAGE = "http://www.csmonitor.com/World/Americas/2011/0321/Haiti-elects-new-president-for-Herculean-task/(page)/2";

	private Source washingtonPost;

	private Source csMonitor;

	@Before
	public void setUp() {
		washingtonPost = Source.WASHINGTON_POST;
		csMonitor = Source.CS_MONITOR;
	}

	@Test
	public void testNoArgumentUrl() {
		for (Source source : Source.values()) {
			assertEquals(source.toString(), COMMON_URL,
					source.canonicalize(COMMON_URL));
		}
	}

	@Test
	public void testArgumentUrl() {
		for (Source source : Source.values()) {
			assertEquals(source.toString(), COMMON_URL,
					source.canonicalize(COMMON_URL + "?hp"));
		}
	}

	@Test
	public void testMultiArgumentUrl() {
		for (Source source : Source.values()) {
			assertEquals(source.toString(), COMMON_URL,
					source.canonicalize(COMMON_URL + "?_r=1&pagewanted=all"));
		}
	}

	@Test
	public void testWashingtonPost() {
		// test pages
		for (int i = 1; i <= 20; i++) {
			String pageUrl = "http://www.washingtonpost.com/opinions/as-global-crises-mount-obama-has-become-the-worlds-master-of-ceremonies-/2011/03/15/ABAKbLs_story_"
					+ i + ".html";
			assertEquals(WP_STORY, washingtonPost.canonicalize(pageUrl));
		}

		// test printable version
		String printableUrl = "http://www.washingtonpost.com/opinions/as-global-crises-mount-obama-has-become-the-worlds-master-of-ceremonies-/2011/03/15/ABAKbLs_print.html";
		assertEquals(WP_STORY, washingtonPost.canonicalize(printableUrl));

		// test single page version
		String singlePageUrl = "http://www.washingtonpost.com/opinions/as-global-crises-mount-obama-has-become-the-worlds-master-of-ceremonies-/2011/03/15/ABAKbLs_singlePage.html";
		assertEquals(WP_STORY, washingtonPost.canonicalize(singlePageUrl));

		// make sure that story and blog URLs are canonicalized to themselves
		assertEquals(WP_STORY, washingtonPost.canonicalize(WP_STORY));
		assertEquals(WP_BLOG, washingtonPost.canonicalize(WP_BLOG));
	}

	@Test
	public void testCsMonitor() {
		assertEquals(CSM_BASE, csMonitor.canonicalize(CSM_PAGE));
	}
}
