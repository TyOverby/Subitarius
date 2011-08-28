/*
 * GetArticleByUrlHandler.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.instance.server.action;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.dozer.Mapper;
import org.slf4j.Logger;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import com.prealpha.dispatch.server.ActionHandler;
import com.prealpha.dispatch.shared.ActionException;
import com.prealpha.dispatch.shared.Dispatcher;
import com.subitarius.domain.Article;
import com.subitarius.domain.ArticleUrl;
import com.subitarius.domain.Article_;
import com.subitarius.instance.shared.action.GetArticleByUrl;
import com.subitarius.instance.shared.action.GetArticleResult;
import com.subitarius.instance.shared.dto.ArticleDto;
import com.subitarius.util.logging.InjectLogger;

class GetArticleByUrlHandler implements
		ActionHandler<GetArticleByUrl, GetArticleResult> {
	@InjectLogger
	private Logger log;

	private final EntityManager entityManager;

	private final Mapper mapper;

	@Inject
	public GetArticleByUrlHandler(EntityManager entityManager, Mapper mapper) {
		this.entityManager = entityManager;
		this.mapper = mapper;
	}

	@Transactional
	@Override
	public GetArticleResult execute(GetArticleByUrl action,
			Dispatcher dispatcher) throws ActionException {
		String articleUrlHash = action.getArticleUrlHash();
		ArticleUrl articleUrl = entityManager.find(ArticleUrl.class,
				articleUrlHash);

		try {
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<Article> criteria = builder
					.createQuery(Article.class);
			Root<Article> articleRoot = criteria.from(Article.class);
			criteria.where(builder.equal(articleRoot.get(Article_.url),
					articleUrl));
			criteria.where(builder.isNull(articleRoot.get(Article_.child)));
			Article article = entityManager.createQuery(criteria)
					.getSingleResult();

			log.info("handled request for article, URL: {}", articleUrl);
			ArticleDto dto = mapper.map(article, ArticleDto.class);
			return new GetArticleResult(dto);
		} catch (NoResultException nrx) {
			log.info("handled request for unparsed article, URL: {}",
					articleUrl);
			return new GetArticleResult(null);
		}
	}
}
