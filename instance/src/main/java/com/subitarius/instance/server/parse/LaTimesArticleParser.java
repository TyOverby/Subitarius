/*
 * LaTimesArticleParser.java
 * Copyright (C) 2011 Ty Overby, Meyer Kizner
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
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.subitarius.domain.Article;
import com.subitarius.domain.ArticleUrl;
import com.subitarius.domain.Team;
import com.subitarius.util.http.RobotsExclusionException;
import com.subitarius.util.http.SimpleHttpClient;

final class LaTimesArticleParser implements ArticleParser {
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat(
			"MMMMM dd, yyyy");

	private final Provider<Team> teamProvider;

	private final SimpleHttpClient httpClient;

	@Inject
	private LaTimesArticleParser(Provider<Team> teamProvider,
			SimpleHttpClient httpClient) {
		this.teamProvider = teamProvider;
		this.httpClient = httpClient;
	}

	@Override
	public Article parse(ArticleUrl articleUrl) throws ArticleParseException {
		String url = articleUrl.getUrl();
		try {
			List<Document> documents = Lists.newArrayList();
			Document document;
			String canonicalUrl;
			String pageUrl = url;
			int page = 1;
			boolean hasNextPage;
			do {
				InputStream stream = httpClient.doGet(pageUrl);
				document = Jsoup.parse(stream, null, pageUrl);
				documents.add(document);
				canonicalUrl = document.select("meta[property=og:url]").attr(
						"content");

				pageUrl = url + '/' + (++page);
				Element pageControl = document.select("div.mod-pagination")
						.first();
				if (pageControl != null) {
					hasNextPage = pageControl.text().contains("Next");
				} else {
					hasNextPage = false;
				}
			} while (hasNextPage);

			List<Article> articles = Lists.newArrayList();
			String subdomain = canonicalUrl.substring(
					canonicalUrl.indexOf("http://") + 7,
					canonicalUrl.indexOf(".latimes.com"));
			for (Document doc : documents) {
				Article article;
				if (subdomain.equals("articles")) {
					article = parseFeatured(articleUrl, doc);
				} else if (subdomain.equals("latimesblogs")
						|| subdomain.equals("opinion")
						|| subdomain.equals("lakersblog")) {
					article = parseBlog(articleUrl, doc);
				} else {
					article = parseStandard(articleUrl, doc);
				}
				articles.add(article);
			}
			return combine(articles);
		} catch (IOException iox) {
			throw new ArticleParseException(articleUrl, iox);
		} catch (RobotsExclusionException rex) {
			throw new ArticleParseException(articleUrl, rex);
		}
	}

	private Article parseStandard(ArticleUrl articleUrl, Document document)
			throws ArticleParseException {
		String title = document.select("div.story > h1").first().text();
		Element bylineElem = document.select("span.byline").first();
		String byline;
		if (bylineElem != null) {
			byline = bylineElem.text().replace(", Los Angeles Times", "")
					.trim();
		} else {
			byline = null;
		}
		String dateStr = document.select("span.dateString").first().text();
		Date date;
		try {
			date = DATE_FORMAT.parse(dateStr);
		} catch (ParseException px) {
			throw new ArticleParseException(articleUrl, px);
		}

		// they are absolutely evil and separate paragraphs only with <br><br>
		// as a result, we have to iterate through the nodes to find these tags
		// TODO: this method inserts some extra whitespace around links
		List<String> paragraphs = Lists.newArrayList();
		List<Node> nodes = document.select("div#story-body-text").first()
				.childNodes();
		String currentPara = "";
		int lineBreakCount = 0;
		for (Node node : nodes) {
			if (node instanceof Element) {
				if (((Element) node).tagName().equals("br")) {
					lineBreakCount++;
				}
			}
			if (lineBreakCount >= 2) {
				paragraphs.add(currentPara.trim());
				currentPara = "";
				lineBreakCount = 0;
			} else if (node instanceof Element) {
				String nodeText = ((Element) node).text().replace("  ", " ")
						.trim();
				if (!nodeText.isEmpty()) {
					currentPara += nodeText + ' ';
				}
			} else if (node instanceof TextNode) {
				String nodeText = ((TextNode) node).getWholeText()
						.replace("  ", " ").trim();
				if (!nodeText.isEmpty()) {
					currentPara += nodeText + ' ';
				}
			}
		}
		paragraphs.add(currentPara.trim());

		return new Article(teamProvider.get(), articleUrl, title, byline, date,
				paragraphs);
	}

	private Article parseFeatured(ArticleUrl articleUrl, Document document)
			throws ArticleParseException {
		String title = document.select(".multi-line-title-1").first().text();
		String[] metaStr = document.select("#mod-article-byline").first()
				.text().split("\\|");
		String byline;
		if (metaStr.length > 1) {
			byline = metaStr[1].replace(", Los Angeles Times", "").trim();
		} else {
			byline = null;
		}

		Date date;
		try {
			String dateStr = metaStr[0].trim();
			date = DATE_FORMAT.parse(dateStr);
		} catch (ParseException px) {
			throw new ArticleParseException(articleUrl, px);
		}

		List<String> paragraphs = Lists.newArrayList();
		List<Element> elements = document.select(".mod-articletext > p");
		for (Element elem : elements) {
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

	private Article parseBlog(ArticleUrl articleUrl, Document document)
			throws ArticleParseException {
		String title = document.select("h1.entry-header").text();
		String dateStr = document.select("div.time").first().text();
		Date date;

		try {
			date = DATE_FORMAT.parse(dateStr);
		} catch (ParseException px) {
			throw new ArticleParseException(articleUrl, px);
		}

		String byline = null;
		List<String> paragraphs = Lists.newArrayList();
		boolean inContent = true;
		for (Element elem : document.select("div.entry-body > p")) {
			String text = elem.text().replace('\u00a0', ' ').trim();
			if (text.equals("RELATED:") || text.equals("ALSO:")) {
				inContent = false;
			}
			if (inContent && !text.isEmpty()) {
				paragraphs.add(text);
			}
		}

		return new Article(teamProvider.get(), articleUrl, title, byline, date,
				paragraphs);
	}
}
