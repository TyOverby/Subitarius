/*
 * GetMappingsByArticleHandler.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.server.action;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.dozer.Mapper;
import org.slf4j.Logger;

import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import com.prealpha.dispatch.server.ActionHandler;
import com.prealpha.dispatch.shared.ActionException;
import com.prealpha.dispatch.shared.Dispatcher;
import com.prealpha.extempdb.instance.domain.Article;
import com.prealpha.extempdb.instance.domain.TagMapping;
import com.prealpha.extempdb.instance.shared.action.GetMappingsByArticle;
import com.prealpha.extempdb.instance.shared.action.GetMappingsResult;
import com.prealpha.extempdb.instance.shared.dto.TagMappingDto;
import com.prealpha.extempdb.instance.util.logging.InjectLogger;

class GetMappingsByArticleHandler implements
		ActionHandler<GetMappingsByArticle, GetMappingsResult> {
	@InjectLogger
	private Logger log;

	private final EntityManager entityManager;

	private final Mapper mapper;

	@Inject
	public GetMappingsByArticleHandler(EntityManager entityManager,
			Mapper mapper) {
		this.entityManager = entityManager;
		this.mapper = mapper;
	}

	@Transactional
	@Override
	public GetMappingsResult execute(GetMappingsByArticle action,
			Dispatcher dispatcher) throws ActionException {
		Long articleId = action.getArticleId();
		Article article = entityManager.find(Article.class, articleId);
		List<TagMappingDto> dtos = new ArrayList<TagMappingDto>();

		for (TagMapping mapping : article.getMappings()) {
			dtos.add(mapper.map(mapping, TagMappingDto.class));
		}

		List<TagMappingDto.Key> mappingKeys = new ArrayList<TagMappingDto.Key>();
		for (TagMappingDto dto : Iterables.filter(dtos, action)) {
			mappingKeys.add(dto.getKey());
		}

		log.info(
				"handled request for mappings to article ID {}, returning {} mapping keys",
				articleId, mappingKeys.size());

		return new GetMappingsResult(mappingKeys);
	}
}
