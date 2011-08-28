/*
 * PatternDirectiveTest.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.util.http.robots;

import static org.junit.Assert.*;

import org.junit.Test;

import com.google.common.base.Predicate;
import com.subitarius.util.http.robots.Directive.Type;

public class PatternDirectiveTest {
	@Test
	public void testEmptyPattern() {
		Predicate<String> predicate = new PatternDirective("", Type.DISALLOW);
		assertFalse(predicate.apply(""));
		assertFalse(predicate.apply("/"));
		assertFalse(predicate.apply("/index.html"));
	}

	@Test
	public void testExplicitWildcard() {
		Predicate<String> predicate = new PatternDirective("*", Type.DISALLOW);
		assertTrue(predicate.apply(""));
		assertTrue(predicate.apply("/"));
		assertTrue(predicate.apply("/index.html"));
	}

	@Test
	public void testRootDirectory() {
		Predicate<String> predicate = new PatternDirective("/", Type.DISALLOW);
		assertFalse(predicate.apply(""));
		assertTrue(predicate.apply("/"));
		assertTrue(predicate.apply("/index.html"));
		assertTrue(predicate.apply("/foo/bar/"));
		assertTrue(predicate.apply("/foo/bar/index.html"));
	}

	@Test
	public void testSubDirectory() {
		Predicate<String> predicate = new PatternDirective("/foo/bar/",
				Type.DISALLOW);
		assertFalse(predicate.apply(""));
		assertFalse(predicate.apply("/"));
		assertFalse(predicate.apply("/index.html"));
		assertTrue(predicate.apply("/foo/bar/"));
		assertTrue(predicate.apply("/foo/bar/index.html"));
		assertTrue(predicate.apply("/foo/bar/baz.png"));
	}

	@Test
	public void testPage() {
		Predicate<String> predicate = new PatternDirective("/index.html",
				Type.DISALLOW);
		assertFalse(predicate.apply(""));
		assertFalse(predicate.apply("/"));
		assertTrue(predicate.apply("/index.html"));
		assertFalse(predicate.apply("/foo/bar/index.html"));
	}

	@Test
	public void testSubDirectoryPage() {
		Predicate<String> predicate = new PatternDirective(
				"/foo/bar/index.html", Type.DISALLOW);
		assertFalse(predicate.apply(""));
		assertFalse(predicate.apply("/"));
		assertFalse(predicate.apply("/index.html"));
		assertFalse(predicate.apply("/foo/bar/"));
		assertTrue(predicate.apply("/foo/bar/index.html"));
		assertFalse(predicate.apply("/foo/bar/baz.png"));
	}

	@Test
	public void testFileExtension() {
		Predicate<String> predicate = new PatternDirective("/*.png$",
				Type.DISALLOW);
		assertFalse(predicate.apply(""));
		assertFalse(predicate.apply("/"));
		assertFalse(predicate.apply("/index.html"));
		assertTrue(predicate.apply("/foo/bar/baz.png"));
		assertFalse(predicate.apply("/foo/.png/index.html"));
	}

	@Test
	public void testArbitraryWildcard() {
		Predicate<String> predicate = new PatternDirective("/*bar*",
				Type.DISALLOW);
		assertFalse(predicate.apply(""));
		assertFalse(predicate.apply("/"));
		assertTrue(predicate.apply("/foo/bar/"));
		assertTrue(predicate.apply("/foo/bar/index.html"));
		assertTrue(predicate.apply("/foo/bar/baz.png"));
		assertFalse(predicate.apply("/foo/.png/index.html"));
	}

	@Test
	public void testGeneralUserAgent() {
		Predicate<String> predicate = new PatternDirective("Googlebot",
				Type.USER_AGENT);
		assertFalse(predicate.apply(""));
		assertFalse(predicate.apply("/"));
		assertFalse(predicate.apply("/index.html"));
		assertTrue(predicate.apply("Googlebot"));
		assertTrue(predicate.apply("Googlebot-Image"));
	}

	@Test
	public void testSpecificUserAgent() {
		Predicate<String> predicate = new PatternDirective("Googlebot-Image",
				Type.USER_AGENT);
		assertFalse(predicate.apply(""));
		assertFalse(predicate.apply("/"));
		assertFalse(predicate.apply("/index.html"));
		assertFalse(predicate.apply("Googlebot"));
		assertTrue(predicate.apply("Googlebot-Image"));
	}
}
