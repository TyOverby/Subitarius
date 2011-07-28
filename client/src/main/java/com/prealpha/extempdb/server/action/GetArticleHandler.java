/*
 * GetArticleHandler.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server.action;

import javax.persistence.EntityManager;

import org.dozer.Mapper;
import org.slf4j.Logger;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import com.prealpha.dispatch.server.ActionHandler;
import com.prealpha.dispatch.shared.ActionException;
import com.prealpha.dispatch.shared.Dispatcher;
import com.prealpha.extempdb.domain.Article;
import com.prealpha.extempdb.shared.action.GetArticle;
import com.prealpha.extempdb.shared.action.GetArticleResult;
import com.prealpha.extempdb.shared.dto.ArticleDto;
import com.prealpha.extempdb.util.logging.InjectLogger;

class GetArticleHandler implements ActionHandler<GetArticle, GetArticleResult> {
	@InjectLogger
	private Logger log;

	private final EntityManager entityManager;

	private final Mapper mapper;

	@Inject
	public GetArticleHandler(EntityManager entityManager, Mapper mapper) {
		this.entityManager = entityManager;
		this.mapper = mapper;
	}

	@Transactional
	@Override
	public GetArticleResult execute(GetArticle action, Dispatcher dispatcher)
			throws ActionException {
		Long articleId = action.getArticleId();
		Article article = entityManager.find(Article.class, articleId);

		if (article == null) {
			log.info("handled request for non-existent article, ID {}",
					articleId);
			return new GetArticleResult(null);
		} else {
			log.info("handled request for article, ID {}", articleId);
			ArticleDto dto = mapper.map(article, ArticleDto.class);
			return new GetArticleResult(dto);
		}
	}
}
