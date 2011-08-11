/*
 * AddArticleHandler.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.server.action;

import javax.persistence.EntityManager;

import org.slf4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.prealpha.dispatch.server.ActionHandler;
import com.prealpha.dispatch.shared.ActionException;
import com.prealpha.dispatch.shared.Dispatcher;
import com.prealpha.extempdb.domain.Article;
import com.prealpha.extempdb.domain.ArticleUrl;
import com.prealpha.extempdb.domain.Team;
import com.prealpha.extempdb.instance.server.parse.ArticleParseException;
import com.prealpha.extempdb.instance.server.parse.ArticleParser;
import com.prealpha.extempdb.instance.server.parse.ParserNotFoundException;
import com.prealpha.extempdb.instance.shared.action.AddArticle;
import com.prealpha.extempdb.instance.shared.action.AddArticleResult;
import com.prealpha.extempdb.instance.shared.action.AddArticleResult.Type;
import com.prealpha.extempdb.util.logging.InjectLogger;

class AddArticleHandler implements ActionHandler<AddArticle, AddArticleResult> {
	@InjectLogger
	private Logger log;

	private final EntityManager entityManager;

	private final ArticleParser articleParser;

	private final Provider<Team> teamProvider;

	@Inject
	public AddArticleHandler(EntityManager entityManager,
			ArticleParser articleParser, Provider<Team> teamProvider) {
		this.entityManager = entityManager;
		this.articleParser = articleParser;
		this.teamProvider = teamProvider;
	}

	@Override
	public AddArticleResult execute(AddArticle action, Dispatcher dispatcher)
			throws ActionException {
		String url = action.getUrl();
		ArticleUrl articleUrl = new ArticleUrl(teamProvider.get(), url);
		try {
			Article article = articleParser.parse(articleUrl);
			if (article == null) {
				log.info("no valid article found at URL: {}", url);
				return new AddArticleResult(Type.NO_ARTICLE);
			} else {
				log.info("created and persisted article with hash: {}",
						article.getHash());
				entityManager.persist(article);
				return new AddArticleResult(Type.SUCCESS, article.getHash());
			}
		} catch (ParserNotFoundException pnfx) {
			log.info("no parser found for URL: {}", url);
			return new AddArticleResult(Type.NO_PARSER);
		} catch (ArticleParseException apx) {
			log.warn("parse failed for URL: {}", url);
			return new AddArticleResult(Type.PARSE_FAILED);
		}
	}
}
