/*
 * GetArticleByUrlHandler.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.instance.server.action;

import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.dozer.Mapper;
import org.slf4j.Logger;

import com.google.inject.Inject;
import com.prealpha.xylophone.server.ActionHandler;
import com.prealpha.xylophone.shared.ActionException;
import com.subitarius.action.GetArticleByUrl;
import com.subitarius.action.GetArticleResult;
import com.subitarius.action.dto.ArticleDto;
import com.subitarius.domain.Article;
import com.subitarius.domain.ArticleUrl_;
import com.subitarius.domain.Article_;
import com.subitarius.domain.DistributedEntity;
import com.subitarius.util.logging.InjectLogger;

final class GetArticleByUrlHandler implements
		ActionHandler<GetArticleByUrl, GetArticleResult> {
	@InjectLogger
	private Logger log;

	private final EntityManager entityManager;

	private final Mapper mapper;

	@Inject
	private GetArticleByUrlHandler(EntityManager entityManager, Mapper mapper) {
		this.entityManager = entityManager;
		this.mapper = mapper;
	}

	@Override
	public GetArticleResult execute(GetArticleByUrl action)
			throws ActionException {
		String articleUrl = action.getArticleUrl();
		try {
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<Article> criteria = builder
					.createQuery(Article.class);
			Root<Article> articleRoot = criteria.from(Article.class);
			criteria.where(builder.and(builder.equal(
					articleRoot.get(Article_.url).get(ArticleUrl_.url),
					articleUrl)), builder.isEmpty(articleRoot
					.<Set<DistributedEntity>> get("children")));
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
