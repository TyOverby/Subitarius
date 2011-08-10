/*
 * WaPostArticleParser.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.server.parse;

import static com.google.common.base.Preconditions.*;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.prealpha.extempdb.util.http.RobotsExclusionException;
import com.prealpha.extempdb.util.http.SimpleHttpClient;

final class WaPostArticleParser extends AbstractArticleParser {
	private static enum ArticleType {
		STORY {
			@Override
			String getBodySelector() {
				return "div.article_body p";
			}
		},

		BLOG {
			@Override
			String getBodySelector() {
				return "div#entrytext p";
			}
		};

		abstract String getBodySelector();
	}

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd");

	private final SimpleHttpClient httpClient;

	@Inject
	private WaPostArticleParser(SimpleHttpClient httpClient) {
		this.httpClient = httpClient;
	}

	@Override
	public String getCanonicalUrl(String url) {
		url = super.getCanonicalUrl(url);

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
				List<Document> documents = Lists.newArrayList();
				Document document;
				int page = 0;
				do {
					InputStream stream = httpClient.doGet(url, params);
					document = Jsoup.parse(stream, null, url);
					documents.add(document);

					int index;
					if (page > 0) {
						index = url.indexOf("_" + page + ".html");
					} else {
						index = url.indexOf(".html");
					}

					String suffix = "_" + (++page) + ".html";
					url = url.substring(0, index) + suffix;
				} while (!document.select("a.next-page").isEmpty());

				List<ProtoArticle> articles = Lists.newArrayList();
				for (Document doc : documents) {
					articles.add(getFromHtml(doc, ArticleType.STORY));
				}
				return combine(articles);
			} else if (url.endsWith("_blog.html")) {
				InputStream stream = httpClient.doGet(url, params);
				Document document = Jsoup.parse(stream, null, url);
				return getFromHtml(document, ArticleType.BLOG);
			} else {
				// we don't know how to deal with this
				return null;
			}
		} catch (ParseException px) {
			throw new ArticleParseException(url, px);
		} catch (IOException iox) {
			throw new ArticleParseException(url, iox);
		} catch (RobotsExclusionException rex) {
			throw new ArticleParseException(url, rex);
		}
	}

	private static ProtoArticle getFromHtml(Document document, ArticleType type)
			throws ParseException {
		String title = document.select("meta[name=DC.title]").attr("content")
				.trim();
		String creator = document.select("meta[name=DC.creator]").attr(
				"content");
		String byline;
		if (creator.isEmpty()) {
			byline = null;
		} else {
			byline = "By " + creator;
		}

		String dateStr = document.select("meta[name=DC.date.issued]").attr(
				"content");
		Date date = DATE_FORMAT.parse(dateStr);

		List<String> paragraphs = Lists.newArrayList();
		String bodySelector = type.getBodySelector();
		List<Element> elements = document.select(bodySelector);
		for (Element elem : elements) {
			// remove image captions
			document.select("span.imgleft, span.imgright, span.blog_caption")
					.remove();
			String text = elem.text().trim();
			if (!text.isEmpty()) {
				paragraphs.add(text);
			}
		}

		return new ProtoArticle(title, byline, date, paragraphs);
	}

	private static ProtoArticle combine(List<ProtoArticle> articles) {
		checkArgument(articles.size() > 0);

		String title = articles.get(0).getTitle();
		String byline = articles.get(0).getByline();
		Date date = articles.get(0).getDate();
		List<String> paragraphs = Lists.newArrayList();

		for (ProtoArticle article : articles) {
			checkArgument(title.equals(article.getTitle()));
			if (byline == null) {
				checkArgument(article.getByline() == null);
			} else {
				checkArgument(byline.equals(article.getByline()));
			}
			checkArgument(date.equals(article.getDate()));
			paragraphs.addAll(article.getParagraphs());
		}

		return new ProtoArticle(title, byline, date, paragraphs);
	}
}
