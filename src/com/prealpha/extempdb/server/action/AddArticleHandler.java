/*
 * AddArticleHandler.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server.action;

import java.net.URISyntaxException;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;

import com.google.inject.Inject;
import com.prealpha.dispatch.server.ActionHandler;
import com.prealpha.dispatch.shared.ActionException;
import com.prealpha.dispatch.shared.Dispatcher;
import com.prealpha.extempdb.server.InjectLogger;
import com.prealpha.extempdb.server.domain.Article;
import com.prealpha.extempdb.server.domain.User;
import com.prealpha.extempdb.server.parse.ArticleParseException;
import com.prealpha.extempdb.server.search.ArticleProcessor;
import com.prealpha.extempdb.server.search.ParserNotFoundException;
import com.prealpha.extempdb.shared.action.AddArticle;
import com.prealpha.extempdb.shared.action.AddArticleResult;
import com.prealpha.extempdb.shared.action.AddArticleResult.Type;
import com.wideplay.warp.persist.Transactional;

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

	@Transactional
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
				return new AddArticleResult(Type.NO_ARTICLE);
			} else {
				return new AddArticleResult(Type.SUCCESS, article.getId());
			}
		} catch (ArticleParseException apx) {
			return new AddArticleResult(Type.PARSE_FAILED);
		} catch (ClassNotFoundException cnfx) {
			return new AddArticleResult(Type.NO_PARSER);
		} catch (ParserNotFoundException pnfx) {
			return new AddArticleResult(Type.NO_PARSER);
		} catch (URISyntaxException usx) {
			return new AddArticleResult(Type.INVALID_URL);
		}
	}
}