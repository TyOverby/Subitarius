/*
 * TheEconomistArticleParser.java
 * Copyright (C) 2011 Meyer Kizner, Ty Overby
 * All rights reserved.
 */

package com.prealpha.extempdb.server.parse;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.*;

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

class TheEconomistArticleParser extends AbstractArticleParser {
	/*
	 * Package visibility for unit testing.
	 */
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("MMM dd yyyy");

	private URL url;
	private final HttpClient httpClient;
	private final Tidy tidy;
	private final DOMBuilder builder;

	@Inject
	public TheEconomistArticleParser(HttpClient httpClient, Tidy tidy,
			DOMBuilder builder) {
		this.httpClient = httpClient;
		this.tidy = tidy;
		this.builder = builder;
	}

	@Override
	public ProtoArticle parse(String url) throws ArticleParseException {
		checkNotNull(url);
		try {
			Map<String, String> params = Collections.emptyMap();
			this.url = new URL(url);
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
		Element titleElement = HtmlHelper.getMatches(document, "div", "class", "headline").get(0);
		title = titleElement.getValue();

		//get the date
		Date date = null;
		String dateString;
		try
		{
			Element dateElement = HtmlHelper.getMatches(document, "p", "class", "ec-article-info").get(0);
			dateString = dateElement.getText().replace("th", "").replace("st", "").replace("rd", "").replace("nd", "");
			try
			{
				date = DATE_FORMAT.parse(dateString);
			}
			catch(ParseException e)
			{
				e.printStackTrace();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		// get the byline, if there is one
		String byline = null;
		try
		{
			Element byLineElement = HtmlHelper.getMatches(document, "a", "class", "contributor").get(0);
			byline = byLineElement.getValue();
		} 
		catch(Exception e) 
		{
			byline = null;
		}
	
		
		
		
		

		// get the body text
		Filter bodyElementFilter = XmlUtils.getElementFilter("div", "class","ec-article-content clear");
		Iterator<?> i1 = document.getDescendants(bodyElementFilter);
		List<String> paragraphs = new ArrayList<String>();
		while (i1.hasNext()) {
			Element bodyElement = (Element) i1.next();

			Filter paragraphFilter = XmlUtils.getElementFilter("p", null, null);
			Iterator<?> i2 = bodyElement.getDescendants(paragraphFilter);
			while (i2.hasNext()) {
				Element paragraph = (Element) i2.next();
				String text = paragraph.getTextTrim();
				if(!text.isEmpty())
				{
					paragraphs.add(text);
				}
			}
		}

		return new ProtoArticle(title, byline, date, paragraphs);
	}
}
