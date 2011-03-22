/*
 * EconomistArticleParser.java
 * Copyright (C) 2011 Meyer Kizner, Ty Overby
 * All rights reserved.
 */

package com.prealpha.extempdb.server.parse;

import static com.google.common.base.Preconditions.*;

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
import java.util.regex.Pattern;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.filter.Filter;

import com.google.inject.Inject;
import com.google.inject.matcher.Matcher;
import com.prealpha.extempdb.server.http.HttpClient;
import com.prealpha.extempdb.server.http.RobotsExclusionException;

class EconomistArticleParser extends AbstractArticleParser {
	/*
	 * Package visibility for unit testing.
	 */
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat(
			"MMM dd yyyy");
	
	private boolean isPrint = true;
	private final Pattern pattern = Pattern.compile("by (.+) \\|");

	private final HttpClient httpClient;

	@Inject
	public EconomistArticleParser(HttpClient httpClient) {
		this.httpClient = httpClient;
	}

	@Override
	public ProtoArticle parse(String url) throws ArticleParseException {
		checkNotNull(url);
		try {
			Map<String, String> params = Collections.emptyMap();
			InputStream stream = httpClient.doGet(url, params);
			if(url.contains("blogs"))
			{
				this.isPrint=false;
			}
			
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

		// get the title
		String title;
		if(this.isPrint)
		{
			Element titleElement = ParseUtils.searchDescendants(document, "div", "class","headline").get(0);
			Element subTitleElement = ParseUtils.searchDescendants(document, "h1", "class","rubric").get(0);
			title = titleElement.getValue()+": "+subTitleElement.getValue();
		}
		else
		{
			Element titleElement = ParseUtils.searchDescendants(document, "h1", "class","ec-blog-headline").get(0);
			Element subTitleElement = ParseUtils.searchDescendants(document, "h2", "class","ec-blog-fly-title").get(0);
			title = titleElement.getValue()+": "+subTitleElement.getValue();
		}
		
		//get the byline
		String byline = null;
		if(!this.isPrint)
		{
			try
			{
				String blogInfo =  ParseUtils.searchDescendants(document, "p", "class","ec-blog-info").get(0).getValue();
				java.util.regex.Matcher m = pattern.matcher(blogInfo);
				m.find();
				byline = m.group(1);
			}
			catch(Exception e)
			{
				//do nothing, byline is already null
			}
		}

		// get the date
		Date date = null;
		String dateString;
		try {
			Element dateElement = null;
			if(this.isPrint)
			{
				dateElement = ParseUtils.searchDescendants(document, "p", "class","ec-article-info").get(0);
			}
			else 
			{
				dateElement = ParseUtils.searchDescendants(document, "p", "class","ec-blog-info").get(0);

			}
			dateString = dateElement.getText().replace("th", "").replace("st", "").replace("rd", "").replace("nd", "");
			
			date = DATE_FORMAT.parse(dateString);

		} catch (Exception e) {
			e.printStackTrace();
		}

		// get the body text
		Filter bodyElementFilter = null;
		if(this.isPrint)
		{
			bodyElementFilter = ParseUtils.getElementFilter("div", "class","ec-article-content clear");
		}
		else
		{
			bodyElementFilter = ParseUtils.getElementFilter("div", "class","ec-blog-body");
		}
		Iterator<?> i1 = document.getDescendants(bodyElementFilter);
		List<String> paragraphs = new ArrayList<String>();
		while (i1.hasNext()) {
			Element bodyElement = (Element) i1.next();

			Filter paragraphFilter = ParseUtils.getElementFilter("p", null,
					null);
			Iterator<?> i2 = bodyElement.getDescendants(paragraphFilter);
			while (i2.hasNext()) {
				Element paragraph = (Element) i2.next();
				String text = paragraph.getTextTrim();
				if (!text.isEmpty()) {
					paragraphs.add(text);
				}
			}
		}
		return new ProtoArticle(title, byline, date, paragraphs);
	}
}
