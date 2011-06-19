/*
 * GuardianArticleParser.java
 * Copyright (C) 2011 Meyer Kizner, Ty Overby
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
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;

import com.google.inject.Inject;
import com.prealpha.extempdb.server.http.HttpClient;
import com.prealpha.extempdb.server.http.RobotsExclusionException;

final class GuardianArticleParser extends AbstractArticleParser {
	private static enum Feed {
		NONE(null, DATE_FORMAT_UK, "p") {
		},

		AP("AP foreign", DATE_FORMAT_US, "p") {
		},

		REUTERS("Reuters", DATE_FORMAT_US, "div") {
		};

		private final String name;

		private final DateFormat dateFormat;

		private final String textElement;

		private Feed(String name, DateFormat dateFormat, String textElement) {
			this.name = name;
			this.dateFormat = dateFormat;
			this.textElement = textElement;
		}

		private static Feed fromName(String name) {
			for (Feed feed : values()) {
				if (name.equals(feed.name)) {
					return feed;
				}
			}
			return NONE;
		}
	}

	private static final DateFormat DATE_FORMAT_UK = new SimpleDateFormat(
			"EEEEE d MMMMM yyyy");

	private static final DateFormat DATE_FORMAT_US = new SimpleDateFormat(
			"EEEEE MMMMM d yyyy");

	private final HttpClient httpClient;

	@Inject
	public GuardianArticleParser(HttpClient httpClient) {
		this.httpClient = httpClient;
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
		Document document = ParseUtils.parse(html);
		Namespace namespace = document.getRootElement().getNamespace();

		// if we have a blog post, find out and return null now
		Element bodyElement = ParseUtils.searchDescendants(document, "body")
				.get(0);
		String classAttr = bodyElement.getAttributeValue("class");
		if (classAttr.contains("blog-post")) {
			return null;
		}

		// get the title
		Element titleElement = ParseUtils.searchDescendants(document, "div",
				"id", "main-article-info").get(0);
		Element heading = titleElement.getChild("h1", namespace);
		String title = heading.getValue();

		// get the byline, if there is one
		List<Element> bylineElements = ParseUtils.searchDescendants(document,
				"a", "class", "contributor");
		String byline;
		if (bylineElements.size() > 0) {
			Element bylineElement = (Element) bylineElements.get(0);
			byline = bylineElement.getValue();
		} else {
			byline = null;
		}

		/*
		 * Get the date. The actual date is in a non-standard <time> element,
		 * which JTidy doesn't like and seems to remove. So we use the parent
		 * element and parse out the date. For example, the parent element might
		 * contain "The Guardian, Thursday 27 January 2011". So we split() on
		 * the comma and take the second fragment for parsing.
		 */
		Element dateElement = ParseUtils.searchDescendants(document, "li",
				"class", "publication").get(0);
		String[] dateSplit = dateElement.getValue().split(",");
		Feed feed = Feed.fromName(dateSplit[0].trim());
		String dateString = dateSplit[1].trim();
		Date date;
		try {
			date = feed.dateFormat.parse(dateString);
		} catch (ParseException px) {
			throw new ArticleParseException(px);
		}

		// get the body text
		List<String> paragraphs = new ArrayList<String>();
		Element articleWrapper = ParseUtils.searchDescendants(document, "div",
				"id", "article-wrapper").get(0);
		List<Element> paragraphElements = ParseUtils.searchDescendants(
				articleWrapper, feed.textElement);
		for (Element paragraph : paragraphElements) {
			// Reuters has an additional wrapper div that we have to skip
			if (paragraph.getChildren(feed.textElement, namespace).isEmpty()) {
				String text = paragraph.getValue().trim();
				if (!text.isEmpty()) {
					paragraphs.add(text);
				}
			}
		}

		return new ProtoArticle(title, byline, date, paragraphs);
	}
}
