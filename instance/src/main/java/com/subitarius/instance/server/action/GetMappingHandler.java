/*
 * GetMappingHandler.java
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
import com.prealpha.dispatch.server.ActionHandler;
import com.prealpha.dispatch.shared.ActionException;
import com.prealpha.dispatch.shared.Dispatcher;
import com.subitarius.action.GetMapping;
import com.subitarius.action.GetMappingResult;
import com.subitarius.action.dto.TagMappingDto;
import com.subitarius.domain.ArticleUrl_;
import com.subitarius.domain.TagMapping;
import com.subitarius.domain.TagMapping_;
import com.subitarius.domain.Tag_;
import com.subitarius.util.logging.InjectLogger;

final class GetMappingHandler implements
		ActionHandler<GetMapping, GetMappingResult> {
	@InjectLogger
	private Logger log;

	private final EntityManager entityManager;

	private final Mapper mapper;

	@Inject
	private GetMappingHandler(EntityManager entityManager, Mapper mapper) {
		this.entityManager = entityManager;
		this.mapper = mapper;
	}

	@Override
	public GetMappingResult execute(GetMapping action, Dispatcher dispatcher)
			throws ActionException {
		String tagName = action.getTagName();
		String articleUrlHash = action.getArticleUrlHash();

		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<TagMapping> criteria = builder
				.createQuery(TagMapping.class);
		Root<TagMapping> mappingRoot = criteria.from(TagMapping.class);
		criteria.where(builder.equal(
				mappingRoot.get(TagMapping_.tag).get(Tag_.name), tagName));
		criteria.where(builder.equal(mappingRoot.get(TagMapping_.articleUrl)
				.get(ArticleUrl_.hash), articleUrlHash));
		try {
			TagMapping mapping = entityManager.createQuery(criteria)
					.getSingleResult();
			log.info("handled request for tag mapping: {}", mapping);
			TagMappingDto mappingDto = mapper.map(mapping, TagMappingDto.class);
			return new GetMappingResult(mappingDto);
		} catch (NoResultException nrx) {
			log.info("handled request for non-existing mapping: [ {} -> {} ]",
					tagName, articleUrlHash);
			return new GetMappingResult(null);
		}
	}
}
