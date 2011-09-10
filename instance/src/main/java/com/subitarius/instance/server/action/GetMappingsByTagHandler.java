/*
 * GetMappingsByTagHandler.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.instance.server.action;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.dozer.Mapper;
import org.slf4j.Logger;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import com.prealpha.dispatch.server.ActionHandler;
import com.prealpha.dispatch.shared.ActionException;
import com.prealpha.dispatch.shared.Dispatcher;
import com.subitarius.action.GetMappingsByTag;
import com.subitarius.action.GetMappingsResult;
import com.subitarius.action.dto.ArticleDto;
import com.subitarius.action.dto.TagMappingDto;
import com.subitarius.domain.Article;
import com.subitarius.domain.Tag;
import com.subitarius.domain.TagMapping;
import com.subitarius.domain.Tag_;
import com.subitarius.util.logging.InjectLogger;

class GetMappingsByTagHandler implements
		ActionHandler<GetMappingsByTag, GetMappingsResult> {
	@InjectLogger
	private Logger log;

	private final EntityManager entityManager;

	private final Mapper mapper;

	@Inject
	public GetMappingsByTagHandler(EntityManager entityManager, Mapper mapper) {
		this.entityManager = entityManager;
		this.mapper = mapper;
	}

	@Transactional
	@Override
	public GetMappingsResult execute(final GetMappingsByTag action,
			Dispatcher dispatcher) throws ActionException {
		String tagName = action.getTagName();
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Tag> criteria = builder.createQuery(Tag.class);
		Root<Tag> root = criteria.from(Tag.class);
		criteria.where(builder.equal(root.get(Tag_.name), tagName));
		Tag tag = entityManager.createQuery(criteria).getSingleResult();

		final Map<String, TagMapping> mappings = Maps.newHashMap();
		for (TagMapping mapping : tag.getMappings()) {
			mappings.put(mapping.getHash(), mapping);
		}

		List<TagMappingDto> dtos = Lists.transform(
				Lists.newArrayList(mappings.values()),
				new Function<TagMapping, TagMappingDto>() {
					@Override
					public TagMappingDto apply(TagMapping input) {
						return mapper.map(input, TagMappingDto.class);
					}
				});
		if (action.getComparator() != null) {
			dtos = Lists.newArrayList(dtos);
			Collections.sort(dtos, new Comparator<TagMappingDto>() {
				@Override
				public int compare(TagMappingDto m1, TagMappingDto m2) {
					String hash1 = m1.getHash();
					String hash2 = m2.getHash();
					Article article1 = mappings.get(hash1).getArticleUrl()
							.getArticle();
					Article article2 = mappings.get(hash2).getArticleUrl()
							.getArticle();
					ArticleDto dto1 = mapper.map(article1, ArticleDto.class);
					ArticleDto dto2 = mapper.map(article2, ArticleDto.class);
					return action.getComparator().compare(dto1, dto2);
				}
			});
		}
		Collections2.filter(dtos, action);
		log.info("returned {} mappings for tag: {}", dtos.size(), tag);
		return new GetMappingsResult(dtos);
	}
}
