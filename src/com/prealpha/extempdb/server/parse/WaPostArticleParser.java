/*
 * WaPostArticleParser.java
 * Copyright (C) 2011 Meyer Kizner
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

import org.apache.http.HttpStatus;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.filter.Filter;
import org.jdom.input.DOMBuilder;
import org.w3c.tidy.Tidy;

import com.google.inject.Inject;
import com.prealpha.extempdb.server.http.HttpClient;
import com.prealpha.extempdb.server.http.RobotsExclusionException;
import com.prealpha.extempdb.server.http.StatusCodeException;

class WaPostArticleParser implements ArticleParser {
	private static enum ArticleType {
		STORY {
			@Override
			Filter getBodyFilter() {
				return ParseUtils.getElementFilter("div", "id", "article_body");
			}
		},

		BLOG {
			@Override
			Filter getBodyFilter() {
				return ParseUtils.getElementFilter("div", "id", "entrytext");
			}
		};

		abstract Filter getBodyFilter();
	}

	/*
	 * Package visibility for unit testing.
	 */
	static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

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
		// strip off any GET parameters before dealing with other stuff
		if (url.contains("?")) {
			url = url.substring(0, url.indexOf("?"));
		}

		if (url.matches(".*_story_\\d+.html")) {
			// page numbers
			int index = url.lastIndexOf("_story");
			return url.substring(0, index) + "_story.html";
		} else if (url.endsWith("_singlePage.html")) {
			// single page version
			int index = url.lastIndexOf("_singlePage");
			return url.substring(0, index) + "_story.html";
		} else if (url.endsWith("_print.html")) {
			// printable version
			int index = url.lastIndexOf("_print");
			return url.substring(0, index) + "_story.html";
		} else {
			return url;
		}
	}

	@Override
	public ProtoArticle parse(String url) throws ArticleParseException {
		checkNotNull(url);

		Map<String, String> params = Collections.emptyMap();

		try {
			if (url.endsWith("_story.html")) {
				/*
				 * The Post's robots.txt file blocks us from fetching the single
				 * page and printable versions of articles. So we fetch all
				 * possible pages, parse them individually, and combine them
				 * together.
				 */
				List<InputStream> streams = new ArrayList<InputStream>();
				streams.add(httpClient.doGet(url, params));

				int page = 1;
				while (true) {
					try {
						int index = url.length() - 5;
						String suffix = "_" + (page++) + ".html";
						String pageUrl = url.substring(0, index) + suffix;
						streams.add(httpClient.doGet(pageUrl, params));
					} catch (IOException iox) {
						Throwable cause = iox.getCause();
						if (cause instanceof StatusCodeException) {
							StatusCodeException scx = (StatusCodeException) cause;
							if (scx.getStatusCode() == HttpStatus.SC_NOT_FOUND) {
								break; // that was the end of the article
							}
						}
						throw iox; // some exception other than a not found
					}
				}

				List<ProtoArticle> articles = new ArrayList<ProtoArticle>();
				for (InputStream stream : streams) {
					articles.add(getFromHtml(stream, ArticleType.STORY));
				}
				return combine(articles);
			} else if (url.endsWith("_blog.html")) {
				InputStream stream = httpClient.doGet(url, params);
				return getFromHtml(stream, ArticleType.BLOG);
			} else {
				throw new IllegalArgumentException(
						"unrecognized canonical URL type");
			}
		} catch (IOException iox) {
			throw new ArticleParseException(iox);
		} catch (RobotsExclusionException rex) {
			throw new ArticleParseException(rex);
		}
	}

	private ProtoArticle getFromHtml(InputStream html, ArticleType type)
			throws ArticleParseException {
		org.w3c.dom.Document doc = tidy.parseDOM(html, null);
		doc.removeChild(doc.getDoctype());
		Document document = builder.build(doc);

		Element headElement = document.getRootElement().getChild("head");
		Map<String, String> metaMap = ParseUtils.getMetaMap(headElement);

		String title = metaMap.get("DC.title");

		String byline;
		if (metaMap.containsKey("DC.creator")) {
			byline = "By " + metaMap.get("DC.creator");
		} else {
			byline = null;
		}

		Date date;
		try {
			date = DATE_FORMAT.parse(metaMap.get("DC.date.issued"));
		} catch (ParseException px) {
			throw new ArticleParseException(px);
		}

		Filter bodyFilter = type.getBodyFilter();
		Element articleBody = (Element) document.getDescendants(bodyFilter)
				.next();
		List<String> paragraphs = new ArrayList<String>();
		for (Object obj : articleBody.getChildren("p")) {
			Element paragraph = (Element) obj;

			// remove image captions
			Filter imageLeftFilter = ParseUtils.getElementFilter("span", "class",
					"imgleft");
			Filter imageRightFilter = ParseUtils.getElementFilter("span",
					"class", "imgright");
			Filter imageFilter = ParseUtils.getOrFilter(imageLeftFilter,
					imageRightFilter);
			Iterator<?> imageIterator = paragraph.getDescendants(imageFilter);
			while (imageIterator.hasNext()) {
				imageIterator.next();
				imageIterator.remove();
			}

			paragraphs.add(paragraph.getValue().trim());
		}

		return new ProtoArticle(title, byline, date, paragraphs);
	}

	private static ProtoArticle combine(List<ProtoArticle> articles) {
		checkArgument(articles.size() > 0);

		String title = articles.get(0).getTitle();
		String byline = articles.get(0).getByline();
		Date date = articles.get(0).getDate();
		List<String> paragraphs = new ArrayList<String>();

		for (ProtoArticle article : articles) {
			checkArgument(title.equals(article.getTitle()));
			checkArgument(byline.equals(article.getByline()));
			checkArgument(date.equals(article.getDate()));
			paragraphs.addAll(article.getParagraphs());
		}

		return new ProtoArticle(title, byline, date, paragraphs);
	}
}
