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
import com.prealpha.extempdb.server.util.HtmlHelper;
import com.prealpha.extempdb.server.util.XmlUtils;

class GuardianArticleParser extends AbstractArticleParser {
	/*
	 * Package visibility for unit testing.
	 */
	static final DateFormat DATE_FORMAT_UK = new SimpleDateFormat(
			"EEEEE d MMMMM yyyy");

	private static final DateFormat DATE_FORMAT_US = new SimpleDateFormat(
			"EEEEE MMMMM d yyyy");

	private final HttpClient httpClient;

	private final Tidy tidy;

	private final DOMBuilder builder;

	@Inject
	public GuardianArticleParser(HttpClient httpClient, Tidy tidy,
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

	private ProtoArticle getFromHtml(InputStream html) throws ArticleParseException {
		
		Document document = HtmlHelper.parse(html);
		Namespace namespace = document.getRootElement().getNamespace();

		// get the title		
		String title;
		Element titleElement = HtmlHelper.getMatches(document, "div", "id", "main-article-info").get(0);
		Element heading = titleElement.getChild("h1", namespace);
		title = heading.getValue();

		// get the byline, if there is one
		// http://www.guardian.co.uk/world/feedarticle/9475892
		String byline;		
		ArrayList<Element> byLineElements = HtmlHelper.getMatches(document, "a", "class", "contributor");
		try
		{
			Element bylineElement = (Element) byLineElements.get(0);
			byline = bylineElement.getValue();
		} 
		catch(Exception e) 
		{
			byline = null;
		}

		/*
		 * Get the date. The actual date is in a non-standard <time> element,
		 * which JTidy doesn't like and seems to remove. So we use the parent
		 * element and parse out the date. For example, the parent element might
		 * contain "The Guardian, Thursday 27 January 2011". So we split() on
		 * the comma and take the second fragment for parsing.
		 */		
		
		Date date;
		Element dateElement = HtmlHelper.getMatches(document, "li", "class", "publication").get(0);
		String dateString = dateElement.getValue().split(",")[1].trim();
		try {
			date = DATE_FORMAT_UK.parse(dateString);
		} catch (ParseException px1) {
			// US style dates are sometimes also used
			// http://www.guardian.co.uk/world/feedarticle/9485574
			try {
				date = DATE_FORMAT_US.parse(dateString);
			} catch (ParseException px2) {
				throw new ArticleParseException(px2);
			}
		}

		// get the body text
		Filter bodyElementFilter = XmlUtils.getElementFilter("div", "id",
				"article-wrapper");
		Iterator<?> i1 = document.getDescendants(bodyElementFilter);
		List<String> paragraphs = new ArrayList<String>();
		while (i1.hasNext()) {
			Element bodyElement = (Element) i1.next();

			Filter paragraphFilter = XmlUtils.getElementFilter("p", null, null);
			Iterator<?> i2 = bodyElement.getDescendants(paragraphFilter);
			while (i2.hasNext()) {
				Element paragraph = (Element) i2.next();
				String text = paragraph.getValue().trim();

				if (!text.isEmpty()) {
					paragraphs.add(text);
				}
			}
		}

		return new ProtoArticle(title, byline, date, paragraphs);
	}
}
