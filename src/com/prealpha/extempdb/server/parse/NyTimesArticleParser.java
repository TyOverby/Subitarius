/*
 * NyTimesArticleParser.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server.parse;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.filter.Filter;
import org.jdom.input.DOMBuilder;
import org.w3c.tidy.Tidy;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.prealpha.extempdb.server.http.HttpClient;
import com.prealpha.extempdb.server.http.RobotsExclusionException;

final class NyTimesArticleParser extends AbstractArticleParser {
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat(
			"yyyyMMdd");

	private static final List<String> UNPARSEABLE_TYPES = ImmutableList.of(
			"Interactive", "blog post", "Subject", "Gift Guide", "Login");

	private final HttpClient httpClient;

	private final Tidy tidy;

	private final DOMBuilder builder;

	@Inject
	public NyTimesArticleParser(HttpClient httpClient, Tidy tidy,
			DOMBuilder builder) {
		this.httpClient = httpClient;
		this.tidy = tidy;
		this.builder = builder;
	}

	@Override
	public ProtoArticle parse(String url) throws ArticleParseException {
		try {
			Map<String, String> params = Collections.singletonMap("pagewanted",
					"all");
			InputStream stream = httpClient.doGet(url, params);
			return getFromHtml(stream);
		} catch (IOException iox) {
			throw new ArticleParseException(iox);
		} catch (RobotsExclusionException rex) {
			throw new ArticleParseException(rex);
		}
	}

	private ProtoArticle getFromHtml(InputStream html)
			throws ArticleParseException {
		org.w3c.dom.Document doc = tidy.parseDOM(html, null);
		doc.removeChild(doc.getDoctype());
		Document document = builder.build(doc);

		// they are evil and sometimes give us an ad page
		// fortunately, the ads have a meta tag that gives us a URL we can use
		Filter adMetaFilter = ParseUtils.getElementFilter("meta", "http-equiv",
				"refresh");
		Iterator<?> adMetaIterator = document.getDescendants(adMetaFilter);
		if (adMetaIterator.hasNext()) {
			Element adMeta = (Element) adMetaIterator.next();
			String content = adMeta.getAttributeValue("content");
			String urlFragment = content.split(";")[1];
			return parse("http://www.nytimes.com" + urlFragment);
		}

		Element headElement = document.getRootElement().getChild("head");
		Map<String, String> metaMap = ParseUtils.getMetaMap(headElement);

		// strangely, the Times sometimes gives us responses of all newlines
		// Tidy parses this into an empty document with a single meta tag
		// if the document only has that meta tag, it's invalid and we skip it
		if (metaMap.size() <= 1) {
			return null;
		}

		String pageType = metaMap.get("PST");
		if (UNPARSEABLE_TYPES.contains(pageType)) {
			return null;
		}

		String title = metaMap.get("hdl_p");
		String byline = metaMap.get("byl");
		Date date;

		try {
			date = DATE_FORMAT.parse(metaMap.get("pdate"));
		} catch (ParseException px) {
			throw new ArticleParseException(px);
		}

		Filter bodyElementFilter = ParseUtils.getElementFilter("div", "class",
				"articleBody");
		Iterator<?> i1 = document.getDescendants(bodyElementFilter);
		List<String> paragraphs = new ArrayList<String>();
		while (i1.hasNext()) {
			Element bodyElement = (Element) i1.next();

			Filter paragraphFilter = ParseUtils.getElementFilter("p", null,
					null);
			Iterator<?> i2 = bodyElement.getDescendants(paragraphFilter);
			while (i2.hasNext()) {
				Element paragraph = (Element) i2.next();
				String text = paragraph.getValue().trim();

				// NY Times uses U+0095 as a dot, which MySQL doesn't like
				text = text.replace('\u0095', '\u2022');

				if (!text.isEmpty()) {
					paragraphs.add(text);
				}
			}
		}

		return new ProtoArticle(title, byline, date, paragraphs);
	}
}
