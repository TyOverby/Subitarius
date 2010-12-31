/*
 * GetMappingsByArticleHandler.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server.action;

import java.util.ArrayList;
import java.util.List;

import org.dozer.Mapper;
import org.slf4j.Logger;

import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import com.prealpha.extempdb.server.InjectLogger;
import com.prealpha.extempdb.server.domain.Article;
import com.prealpha.extempdb.server.domain.TagMapping;
import com.prealpha.extempdb.server.persistence.ArticleDao;
import com.prealpha.extempdb.server.persistence.Transactional;
import com.prealpha.extempdb.shared.action.GetMappingsByArticle;
import com.prealpha.extempdb.shared.action.GetMappingsResult;
import com.prealpha.extempdb.shared.dto.ArticleDto;
import com.prealpha.extempdb.shared.dto.TagMappingDto;
import com.prealpha.extempdb.shared.id.TagMappingId;
import com.prealpha.gwt.dispatch.server.ActionHandler;
import com.prealpha.gwt.dispatch.shared.ActionException;
import com.prealpha.gwt.dispatch.shared.Dispatcher;

class GetMappingsByArticleHandler implements
		ActionHandler<GetMappingsByArticle, GetMappingsResult> {
	@InjectLogger
	private Logger log;

	private final ArticleDao articleDao;

	private final Mapper mapper;

	@Inject
	public GetMappingsByArticleHandler(ArticleDao articleDao, Mapper mapper) {
		this.articleDao = articleDao;
		this.mapper = mapper;
	}

	@Override
	public Class<GetMappingsByArticle> getActionType() {
		return GetMappingsByArticle.class;
	}

	@Transactional
	@Override
	public GetMappingsResult execute(GetMappingsByArticle action,
			Dispatcher dispatcher) throws ActionException {
		ArticleDto articleDto = action.getArticle();
		Article article = articleDao.get(articleDto.getId());
		List<TagMappingDto> dtos = new ArrayList<TagMappingDto>();

		for (TagMapping mapping : article.getMappings()) {
			dtos.add(mapper.map(mapping, TagMappingDto.class));
		}

		List<TagMappingId> ids = new ArrayList<TagMappingId>();

		for (TagMappingDto dto : Iterables.filter(dtos, action)) {
			ids.add(new TagMappingId(dto.getId()));
		}

		log.info(
				"handled request for mappings to article ID {}, returning {} mappings",
				article.getId(), ids.size());

		return new GetMappingsResult(ids);
	}
}
