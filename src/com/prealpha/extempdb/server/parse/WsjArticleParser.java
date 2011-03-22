/*
 * WsjArticleParser.java
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
import org.jdom.Namespace;
import org.jdom.filter.Filter;
import org.jdom.input.DOMBuilder;
import org.w3c.tidy.Tidy;

import com.google.inject.Inject;
import com.prealpha.extempdb.server.http.HttpClient;
import com.prealpha.extempdb.server.http.RobotsExclusionException;

class WsjArticleParser extends AbstractArticleParser {
	private static enum ArticleType {
		COMPLETE, ABRIDGED, NEWSWIRE;
	}

	/*
	 * Package visibility for unit testing.
	 */
	static final DateFormat DATE_FORMAT = new SimpleDateFormat("MMMMM d, yyyy");

	private final HttpClient httpClient;

	private final Tidy tidy;

	private final DOMBuilder builder;

	@Inject
	public WsjArticleParser(HttpClient httpClient, Tidy tidy, DOMBuilder builder) {
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

		/*
		 * The first thing we need to do is figure out what article type we're
		 * dealing with. There are specific meta tags we can look for to figure
		 * this out.
		 */
		ArticleType articleType;
		Namespace namespace = document.getRootElement().getNamespace();
		Element headElement = document.getRootElement().getChild("head",
				namespace);
		Map<String, String> metaMap = ParseUtils.getMetaMap(headElement);

		String miscMarker = metaMap.get("displayname");
		if (miscMarker.equals("Letters")) {
			// letters to the editor
			// http://online.wsj.com/article/SB10001424052748703848204575608741347636192.html
			return null;
		} else if (miscMarker.equals("PR Wire")) {
			// I think this must be News Corp. self-advertising
			// http://online.wsj.com/article/PR-CO-20101117-906923.html
			return null;
		} else if (miscMarker.equals("Journal Concierge")) {
			// "travel guide"
			// http://online.wsj.com/article/SB10001424052702304915104575572663472793840.html
			return null;
		} else if (miscMarker.equals("Spanish")) {
			// self-describing...
			// http://online.wsj.com/article/SB128934068314955423.html
			return null;
		}

		if (metaMap.containsKey("GOOGLEBOT")) {
			if (metaMap.containsKey("description")) {
				articleType = ArticleType.ABRIDGED;
			} else {
				articleType = ArticleType.NEWSWIRE;
			}
		} else {
			articleType = ArticleType.COMPLETE;
		}

		Filter newswireFilter = ParseUtils.getElementFilter("div", "class",
				"articleHeadlineBox headlineType-newswire");
		Filter bylineIconFilter = ParseUtils.getElementFilter("div", "class",
				"articleHeadlineBox headlineType-bylineIcon");
		Filter headlineBoxFilter = ParseUtils.getOrFilter(newswireFilter,
				bylineIconFilter);
		Element headlineBox = (Element) document.getDescendants(
				headlineBoxFilter).next();

		String articleBodyClass;
		switch (articleType) {
		case COMPLETE:
			articleBodyClass = "articlePage";
			break;

		case ABRIDGED:
		case NEWSWIRE:
			articleBodyClass = "article story";
			break;

		default:
			throw new IllegalStateException();
		}

		Filter articleBodyFilter = ParseUtils.getElementFilter("div", "class",
				articleBodyClass);
		Element articleBody = (Element) document.getDescendants(
				articleBodyFilter).next();

		String title = getTitle(headlineBox, namespace);
		String byline = getByline(articleBody, articleType);
		Date date = getDate(headlineBox);
		List<String> paragraphs = getParagraphs(articleBody, namespace);

		return new ProtoArticle(title, byline, date, paragraphs);
	}

	private static String getTitle(Element headlineBox, Namespace namespace) {
		Element headline = headlineBox.getChild("h1", namespace);
		return headline.getValue();
	}

	private static String getByline(Element articleBody, ArticleType articleType) {
		Filter bylineFilter = ParseUtils
				.getElementFilter("h3", "class", "byline");
		Iterator<?> bylineIterator = articleBody.getDescendants(bylineFilter);

		if (!bylineIterator.hasNext()) {
			return null;
		} else {
			String byline = ((Element) bylineIterator.next()).getValue();
			if (byline.isEmpty()) {
				return null;
			} else {
				return byline;
			}
		}
	}

	private static Date getDate(Element headlineBox)
			throws ArticleParseException {
		Filter dateStampFilter = ParseUtils.getElementFilter("li", "class",
				"dateStamp");
		Filter dateStampFirstFilter = ParseUtils.getElementFilter("li", "class",
				"dateStamp first");
		Filter dateElementFilter = ParseUtils.getOrFilter(dateStampFilter,
				dateStampFirstFilter);
		Element dateElement = (Element) headlineBox.getDescendants(
				dateElementFilter).next();
		String dateStr = dateElement.getValue();

		try {
			return DATE_FORMAT.parse(dateStr);
		} catch (ParseException px) {
			throw new ArticleParseException(px);
		}
	}

	private static List<String> getParagraphs(Element articleBody,
			Namespace namespace) {
		// we only want direct children of the article body
		List<String> paragraphs = new ArrayList<String>();
		List<?> paragraphElements = articleBody.getChildren("p", namespace);

		for (Object obj : paragraphElements) {
			Element paragraphElement = (Element) obj;
			String paragraph = paragraphElement.getValue().trim();

			if (!paragraph.isEmpty()) {
				paragraphs.add(paragraph);
			}
		}

		return paragraphs;
	}
}
