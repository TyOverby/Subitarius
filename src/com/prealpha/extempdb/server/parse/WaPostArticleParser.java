/*
 * WaPostArticleParser.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server.parse;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jdom.Content;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.filter.Filter;
import org.jdom.input.DOMBuilder;
import org.w3c.tidy.Tidy;

import com.google.inject.Inject;
import com.prealpha.extempdb.server.http.HttpClient;
import com.prealpha.extempdb.server.http.RobotsExclusionException;
import com.prealpha.extempdb.server.util.XmlUtils;

/*
 * TODO: some articles have "paragraphs" enclosed only by <b> tags...
 */
class WaPostArticleParser extends AbstractArticleParser {
	/*
	 * Package visibility for unit testing.
	 */
	static final DateFormat DATE_FORMAT = new SimpleDateFormat(
			"EEEEE, MMMMM d, yyyy");

	private final HttpClient httpClient;

	private final Tidy tidy;

	private final DOMBuilder builder;

	@Inject
	public WaPostArticleParser(HttpClient httpClient, Tidy tidy,
			DOMBuilder builder) {
		this.httpClient = httpClient;
		this.tidy = tidy;
		this.builder = builder;
	}

	@Override
	public String getCanonicalUrl(String url) {
		url = super.getCanonicalUrl(url);

		while (url.matches(".*_([0-9]|pf).html")) {
			int index = url.lastIndexOf('_');
			url = url.substring(0, index) + ".html";
		}

		return url;
	}

	@Override
	public ProtoArticle parse(String url) throws ArticleParseException {
		// we want the printable version
		url = url.substring(0, url.length() - 5) + "_pf.html";

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
		handleUnescapedMeta(doc);
		Document document = builder.build(doc);

		// they sometimes have corrections at the top of their pages that
		// aren't in the article text; we simply skip the articles for which
		// this is the case
		Filter correctionFilter = XmlUtils.getElementFilter("div", "class",
				"correction");
		Iterator<?> correctionIterator = document
				.getDescendants(correctionFilter);
		if (correctionIterator.hasNext()) {
			return null;
		}

		String title;
		String byline;
		Date date;
		List<String> paragraphs;

		Filter headingFilter = XmlUtils.getElementFilter("font", "size", "+2");
		Iterator<?> headingIterator = document.getDescendants(headingFilter);
		Element heading;
		if (headingIterator.hasNext()) {
			heading = (Element) headingIterator.next();
			title = handleDoubleEncoding(heading.getValue());
		} else {
			return null; // we can't do anything without a title
		}

		Element articleContainer = heading.getParentElement();
		List<?> paragraphElements = articleContainer.getChildren("p");
		Iterator<?> paragraphIterator = paragraphElements.iterator();

		Element bylineElement = ((Element) paragraphIterator.next())
				.getChild("font");
		List<?> bylineContent = bylineElement.getContent();

		// if there's only a single line and a <br>, there's no byline
		if (bylineContent.size() == 2) {
			byline = null;
		} else {
			Content textContent = (Content) bylineContent.get(0);
			byline = handleDoubleEncoding(textContent.getValue());
		}

		int dateIndex = bylineContent.size() - 2;
		String dateStr = ((Content) bylineContent.get(dateIndex)).getValue()
				.trim();

		try {
			date = DATE_FORMAT.parse(dateStr);
		} catch (ParseException px) {
			throw new ArticleParseException(px);
		}

		paragraphs = new ArrayList<String>();
		while (paragraphIterator.hasNext()) {
			Element paragraphElement = (Element) paragraphIterator.next();
			String paragraph = paragraphElement.getValue().trim();
			paragraph = handleDoubleEncoding(paragraph);

			if (!paragraph.isEmpty()) {
				paragraphs.add(paragraph);
			}
		}

		return new ProtoArticle(title, byline, date, paragraphs);
	}

	private static void handleUnescapedMeta(org.w3c.dom.Document doc) {
		org.w3c.dom.NodeList metaList = doc.getElementsByTagName("meta");

		for (int i = 0; i < metaList.getLength(); i++) {
			org.w3c.dom.Element meta = (org.w3c.dom.Element) metaList.item(i);
			org.w3c.dom.NamedNodeMap attributes = meta.getAttributes();

			for (int j = 0; j < attributes.getLength(); j++) {
				org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attributes.item(j);

				if (!attr.getName().equals("name")
						&& !attr.getName().equals("content")
						&& !attr.getName().equals("http-equiv")) {
					meta.removeAttributeNode(attr);
				}
			}
		}
	}

	private static String handleDoubleEncoding(String str) {
		Charset charset = Charset.forName("UTF-8");
		ByteBuffer bb = ByteBuffer.wrap(str.getBytes(charset));
		CharBuffer cb = charset.decode(bb);
		str = cb.toString();

		// MySQL handling
		str = str.replace("\u0097", "\u2014");

		return str;
	}
}
