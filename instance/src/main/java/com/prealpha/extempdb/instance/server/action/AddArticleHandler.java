/*
 * AddArticleHandler.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.server.action;

import java.net.URISyntaxException;

import javax.servlet.http.HttpSession;

import org.apache.catalina.User;
import org.slf4j.Logger;

import com.google.inject.Inject;
import com.prealpha.dispatch.server.ActionHandler;
import com.prealpha.dispatch.shared.ActionException;
import com.prealpha.dispatch.shared.Dispatcher;
import com.prealpha.extempdb.domain.Article;
import com.prealpha.extempdb.instance.server.parse.ArticleParseException;
import com.prealpha.extempdb.instance.shared.action.AddArticle;
import com.prealpha.extempdb.instance.shared.action.AddArticleResult;
import com.prealpha.extempdb.instance.shared.action.AddArticleResult.Type;
import com.prealpha.extempdb.util.logging.InjectLogger;

class AddArticleHandler implements ActionHandler<AddArticle, AddArticleResult> {
	@InjectLogger
	private Logger log;

	private final HttpSession httpSession;

	private final ArticleProcessor articleProcessor;

	@Inject
	public AddArticleHandler(HttpSession httpSession,
			ArticleProcessor articleProcessor) {
		this.httpSession = httpSession;
		this.articleProcessor = articleProcessor;
	}

	@Override
	public AddArticleResult execute(AddArticle action, Dispatcher dispatcher)
			throws ActionException {
		String url = action.getUrl();
		User user = (User) httpSession.getAttribute("user");
		if (user == null) {
			log.info("rejected attempt to add article, URL {} (not logged in)",
					url);
			return new AddArticleResult(Type.PERMISSION_DENIED);
		}

		try {
			Article article = articleProcessor.process(url);
			if (article == null) {
				log.info("article parser rejected page at URL {}", url);
				return new AddArticleResult(Type.NO_ARTICLE);
			} else {
				log.info("user {} successfully requested parsing for URL {}",
						user.getName(), article.getUrl());
				return new AddArticleResult(Type.SUCCESS, article.getId());
			}
		} catch (ArticleParseException apx) {
			log.warn("article parse failed for URL {}", url);
			return new AddArticleResult(Type.PARSE_FAILED);
		} catch (UnsupportedSiteException usx) {
			log.info("unsupported site on URL {}", url);
			return new AddArticleResult(Type.NO_PARSER);
		} catch (URISyntaxException usx) {
			log.info("URL was invalid: {}", url);
			return new AddArticleResult(Type.INVALID_URL);
		}
	}
}
