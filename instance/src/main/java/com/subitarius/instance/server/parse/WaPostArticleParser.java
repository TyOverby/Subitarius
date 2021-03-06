/*
 * WaPostArticleParser.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.instance.server.parse;

import static com.google.common.base.Preconditions.*;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.subitarius.domain.Article;
import com.subitarius.domain.ArticleUrl;
import com.subitarius.domain.Team;
import com.subitarius.util.http.RobotsExclusionException;
import com.subitarius.util.http.SimpleHttpClient;

final class WaPostArticleParser implements ArticleParser {
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd");

	private final Provider<Team> teamProvider;

	private final SimpleHttpClient httpClient;

	@Inject
	private WaPostArticleParser(Provider<Team> teamProvider,
			SimpleHttpClient httpClient) {
		this.teamProvider = teamProvider;
		this.httpClient = httpClient;
	}

	@Override
	public Article parse(ArticleUrl articleUrl) throws ArticleParseException {
		String url = articleUrl.getUrl();
		try {
			/*
			 * The Post's robots.txt file blocks us from fetching the single
			 * page and printable versions of articles. So we fetch all possible
			 * pages, parse them individually, and combine them together.
			 */
			List<Document> documents = Lists.newArrayList();
			Document document;
			int page = 0;
			do {
				InputStream stream = httpClient.doGet(url);
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

			List<Article> articles = Lists.newArrayList();
			for (Document doc : documents) {
				articles.add(parsePage(articleUrl, doc));
			}
			return combine(articles);
		} catch (ParseException px) {
			throw new ArticleParseException(articleUrl, px);
		} catch (IOException iox) {
			throw new ArticleParseException(articleUrl, iox);
		} catch (RobotsExclusionException rex) {
			throw new ArticleParseException(articleUrl, rex);
		}
	}

	private Article parsePage(ArticleUrl articleUrl, Document document)
			throws ParseException {
		String title = document.select("meta[name=DC.title]").attr("content")
				.trim();
		String byline = document.select("meta[name=DC.creator]")
				.attr("content");
		String dateStr = document.select("meta[name=DC.date.issued]").attr(
				"content");
		Date date = DATE_FORMAT.parse(dateStr);

		List<String> paragraphs = Lists.newArrayList();
		List<Element> elements = document
				.select("div.article_body p, div#entrytext > p");
		for (Element elem : elements) {
			// remove image captions
			document.select(
					"span.imgleft, span.imgright, span.blog_caption, span.caption")
					.remove();
			String text = elem.text().trim();
			if (!text.isEmpty()) {
				paragraphs.add(text);
			}
		}

		return new Article(teamProvider.get(), articleUrl, title, byline, date,
				paragraphs);
	}

	private static Article combine(List<Article> articles) {
		checkArgument(articles.size() > 0);

		Team creator = articles.get(0).getCreator();
		ArticleUrl url = articles.get(0).getUrl();
		String title = articles.get(0).getTitle();
		String byline = articles.get(0).getByline();
		Date date = articles.get(0).getDate();
		List<String> paragraphs = Lists.newArrayList();

		for (Article article : articles) {
			checkArgument(creator.equals(article.getCreator()));
			checkArgument(url.equals(article.getUrl()));
			checkArgument(title.equals(article.getTitle()));
			if (byline == null) {
				checkArgument(article.getByline() == null);
			} else {
				checkArgument(byline.equals(article.getByline()));
			}
			checkArgument(date.equals(article.getDate()));
			paragraphs.addAll(article.getParagraphs());
		}

		return new Article(creator, url, title, byline, date, paragraphs);
	}
}
