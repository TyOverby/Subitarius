/*
 * GetArticleByHashHandler.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.instance.server.action;

import javax.persistence.EntityManager;

import org.dozer.Mapper;
import org.slf4j.Logger;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import com.prealpha.dispatch.server.ActionHandler;
import com.prealpha.dispatch.shared.ActionException;
import com.prealpha.dispatch.shared.Dispatcher;
import com.subitarius.action.GetArticleByHash;
import com.subitarius.action.GetArticleResult;
import com.subitarius.action.dto.ArticleDto;
import com.subitarius.domain.Article;
import com.subitarius.util.logging.InjectLogger;

class GetArticleByHashHandler implements
		ActionHandler<GetArticleByHash, GetArticleResult> {
	@InjectLogger
	private Logger log;

	private final EntityManager entityManager;

	private final Mapper mapper;

	@Inject
	public GetArticleByHashHandler(EntityManager entityManager, Mapper mapper) {
		this.entityManager = entityManager;
		this.mapper = mapper;
	}

	@Transactional
	@Override
	public GetArticleResult execute(GetArticleByHash action,
			Dispatcher dispatcher) throws ActionException {
		String articleHash = action.getArticleHash();
		Article article = entityManager.find(Article.class, articleHash);

		if (article == null) {
			log.info("handled request for non-existent article, hash: {}",
					articleHash);
			return new GetArticleResult(null);
		} else {
			log.info("handled request for article, hash: {}", articleHash);
			ArticleDto dto = mapper.map(article, ArticleDto.class);
			return new GetArticleResult(dto);
		}
	}
}
