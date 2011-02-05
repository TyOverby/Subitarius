/*
 * CsmArticleParser.java
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
import com.prealpha.extempdb.server.util.XmlUtils;

class CsmArticleParser extends AbstractArticleParser {
	/*
	 * Package visibility for unit testing.
	 */
	static final DateFormat DATE_FORMAT = new SimpleDateFormat("MMMMM d, yyyy");

	private final HttpClient httpClient;

	private final Tidy tidy;

	private final DOMBuilder builder;

	@Inject
	public CsmArticleParser(HttpClient httpClient, Tidy tidy, DOMBuilder builder) {
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

		// some images have an attribute that messes up JDOM
		// TODO: is there a more efficient way to do this?
		removeAllImages(doc);

		Document document = builder.build(doc);
		Namespace namespace = document.getRootElement().getNamespace();

		Filter containerFilter = XmlUtils.getElementFilter("div", "id",
				"mainColumn");
		Element container = (Element) document.getDescendants(containerFilter)
				.next();

		Element listCheck = container.getChild("div", namespace);
		String listCheckAttr = listCheck.getAttributeValue("class");
		if (listCheckAttr != null && listCheckAttr.equals("list-article-full")) {
			// we hit a "list article", which isn't really parseable
			// http://www.csmonitor.com/USA/Election-2010/2010/1102/Top-10-mistakes-of-Election-2010/Alan-Grayson-s-Taliban-Dan-ad
			// http://www.csmonitor.com/World/Asia-South-Central/2010/1117/Five-reasons-the-US-went-after-merchant-of-death-Viktor-Bout
			return null;
		}

		Filter bodyFilter = XmlUtils.getElementFilter("div", "class", "sBody");
		Filter bodyFilterCfx = XmlUtils.getElementFilter("div", "class",
				"sBody cfx");
		Filter articleBodyFilter = XmlUtils.getOrFilter(bodyFilter,
				bodyFilterCfx);
		Element articleBody = (Element) container.getDescendants(
				articleBodyFilter).next();

		String title = getTitle(container);
		String byline = getByline(container);
		Date date = getDate(container);
		List<String> paragraphs = getParagraphs(articleBody, namespace);

		return new ProtoArticle(title, byline, date, paragraphs);
	}

	private static String getTitle(Element container) {
		Filter headingFilter = XmlUtils.getElementFilter("h1", "class", "head");
		Element heading = (Element) container.getDescendants(headingFilter)
				.next();
		return heading.getValue();
	}

	private static String getByline(Element container) {
		Filter bylineFilter = XmlUtils
				.getElementFilter("p", "class", "sByline");
		Element bylineElement = (Element) container
				.getDescendants(bylineFilter).next();
		String byline = bylineElement.getValue().split("/")[0];

		if (byline.contains(",")) {
			byline = byline.substring(0, byline.indexOf(','));
		}

		return byline;
	}

	private static Date getDate(Element container) throws ArticleParseException {
		Filter bylineFilter = XmlUtils
				.getElementFilter("p", "class", "sByline");
		Element bylineElement = (Element) container
				.getDescendants(bylineFilter).next();
		String[] bylineParts = bylineElement.getValue().split("/");
		int index = bylineParts.length - 1;
		String dateStr = bylineParts[index].trim();

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

	private static void removeAllImages(org.w3c.dom.Node node) {
		org.w3c.dom.NodeList children = node.getChildNodes();

		for (int i = 0; i < children.getLength(); i++) {
			org.w3c.dom.Node child = children.item(i);

			if (child.getLocalName().equals("img")) {
				node.removeChild(child);
			} else if (child.hasChildNodes()) {
				removeAllImages(child);
			}
		}
	}
}
