/*
 * AbstractArticleParserTest.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server.parse;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;

import atunit.AtUnit;
import atunit.Container;
import atunit.Unit;

import com.google.inject.Binder;
import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Provider;

@RunWith(AtUnit.class)
@Container(Container.Option.GUICE)
public class AbstractArticleParserTest implements Module {
	private static final String URL = "http://www.nytimes.com/interactive/2010/11/13/weekinreview/deficits-graphic.html";

	@Unit
	@Inject
	private AbstractArticleParser parser;

	@Override
	public void configure(Binder binder) {
		final AbstractArticleParser parser = new AbstractArticleParser() {
			@Override
			public ProtoArticle parse(String url) throws ArticleParseException {
				return null;
			}
		};

		binder.bind(AbstractArticleParser.class).toProvider(
				new Provider<AbstractArticleParser>() {
					@Override
					public AbstractArticleParser get() {
						return parser;
					}
				});
	}

	@Test
	public void testNoArgumentUrl() {
		String canonical = parser.getCanonicalUrl(URL);
		assertEquals(URL, canonical);
	}

	@Test
	public void testArgumentUrl() {
		String canonical = parser.getCanonicalUrl(URL + "?hp");
		assertEquals(URL, canonical);
	}

	@Test
	public void testMultiArgumentUrl() {
		String canonical = parser.getCanonicalUrl(URL + "?_r=1&pagewanted=all");
		assertEquals(URL, canonical);
	}
}
