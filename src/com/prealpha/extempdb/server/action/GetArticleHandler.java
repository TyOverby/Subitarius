/*
 * GetArticleHandler.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server.action;

import org.dozer.Mapper;
import org.slf4j.Logger;

import com.google.inject.Inject;
import com.prealpha.extempdb.server.InjectLogger;
import com.prealpha.extempdb.server.domain.Article;
import com.prealpha.extempdb.server.persistence.ArticleDao;
import com.prealpha.extempdb.server.persistence.Transactional;
import com.prealpha.extempdb.shared.action.GetArticle;
import com.prealpha.extempdb.shared.action.GetArticleResult;
import com.prealpha.extempdb.shared.dto.ArticleDto;
import com.prealpha.extempdb.shared.id.ArticleId;
import com.prealpha.gwt.dispatch.server.ActionHandler;
import com.prealpha.gwt.dispatch.shared.ActionException;
import com.prealpha.gwt.dispatch.shared.Dispatcher;

class GetArticleHandler implements ActionHandler<GetArticle, GetArticleResult> {
	@InjectLogger
	private Logger log;

	private final ArticleDao articleDao;

	private final Mapper mapper;

	@Inject
	public GetArticleHandler(ArticleDao articleDao, Mapper mapper) {
		this.articleDao = articleDao;
		this.mapper = mapper;
	}

	@Override
	public Class<GetArticle> getActionType() {
		return GetArticle.class;
	}

	@Transactional
	@Override
	public GetArticleResult execute(GetArticle action, Dispatcher dispatcher)
			throws ActionException {
		ArticleId id = action.getId();
		Article article = articleDao.get(id.getId());

		if (article == null) {
			log.info("handled request for non-existent article, ID {}",
					id.getId());
			return new GetArticleResult(null);
		} else {
			log.info("handled request for article, ID {}", id.getId());
			ArticleDto dto = mapper.map(article, ArticleDto.class);
			return new GetArticleResult(dto);
		}
	}
}
