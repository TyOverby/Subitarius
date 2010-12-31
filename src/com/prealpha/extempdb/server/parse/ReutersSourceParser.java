/*
 * ReutersSourceParser.java
 * Copyright (C) 2010 Meyer Kizner
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

import com.google.inject.Inject;
import com.prealpha.extempdb.server.http.HttpClient;
import com.prealpha.extempdb.server.http.RobotsExclusionException;
import com.prealpha.extempdb.server.util.XmlUtils;

class ReutersSourceParser extends AbstractSourceParser {
	/*
	 * Package visibility for unit testing.
	 */
	static final DateFormat DATE_FORMAT = new SimpleDateFormat(
			"EEE MMM d, yyyy hh:mmaa zzz");

	private final HttpClient httpClient;

	private final Tidy tidy;

	private final DOMBuilder builder;

	@Inject
	public ReutersSourceParser(HttpClient httpClient, Tidy tidy,
			DOMBuilder builder) {
		this.httpClient = httpClient;
		this.tidy = tidy;
		this.builder = builder;
	}

	@Override
	public ProtoArticle parse(String url) throws ArticleParseException {
		try {
			Map<String, String> params = Collections.emptyMap();
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

		Filter containerFilter = XmlUtils.getElementFilter("div", "class",
				"column2 gridPanel grid8");
		Element container = (Element) document.getDescendants(containerFilter)
				.next();

		Filter commentFilter = XmlUtils.getElementFilter("div", "class",
				"articleComments");
		Element commentElement = (Element) container.getDescendants(
				commentFilter).next();

		String title = getTitle(container);
		String byline = getByline(container);
		Date date = getDate(container);
		List<String> paragraphs = getParagraphs(container, commentElement);

		return new ProtoArticle(title, byline, date, paragraphs);
	}

	private static String getTitle(Element container) {
		Element heading = container.getChild("h1");
		return heading.getValue();
	}

	private static String getByline(Element container) {
		Filter bylineFilter = XmlUtils.getElementFilter("p", "class", "byline");
		Iterator<?> bylineIterator = container.getDescendants(bylineFilter);

		if (bylineIterator.hasNext()) {
			Element bylineElement = (Element) bylineIterator.next();
			return bylineElement.getValue();
		} else {
			return null;
		}
	}

	private static Date getDate(Element container) throws ArticleParseException {
		Filter dateFilter = XmlUtils.getElementFilter("span", "class",
				"timestamp");
		Element dateElement = (Element) container.getDescendants(dateFilter)
				.next();

		try {
			return DATE_FORMAT.parse(dateElement.getValue());
		} catch (ParseException px) {
			throw new ArticleParseException(px);
		}
	}

	private static List<String> getParagraphs(Element container,
			Element commentElement) {
		List<String> paragraphs = new ArrayList<String>();
		Filter paragraphFilter = XmlUtils.getElementFilter("p", null, null);
		Iterator<?> i1 = container.getDescendants(paragraphFilter);

		// skip through any image caption and credit paragraphs, the byline
		// paragraph, and the date and location paragraph
		boolean parsing = false;
		while (i1.hasNext()) {
			Element paragraph = (Element) i1.next();
			String parentId = paragraph.getParentElement().getAttributeValue(
					"id");

			if ("articleInfo".equals(parentId)) {
				parsing = true;
				continue;
			} else if (parsing && !commentElement.isAncestor(paragraph)) {
				String text = paragraph.getValue().trim();
				if (!text.isEmpty()) {
					paragraphs.add(text);
				}
			}
		}

		return paragraphs;
	}
}
