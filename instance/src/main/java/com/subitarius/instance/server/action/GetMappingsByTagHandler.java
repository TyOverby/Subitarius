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
import java.util.Set;

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
import com.prealpha.dispatch.server.ActionHandler;
import com.prealpha.dispatch.shared.ActionException;
import com.prealpha.dispatch.shared.Dispatcher;
import com.subitarius.action.GetMappingsByTag;
import com.subitarius.action.GetMappingsResult;
import com.subitarius.action.dto.ArticleDto;
import com.subitarius.action.dto.TagMappingDto;
import com.subitarius.domain.Article;
import com.subitarius.domain.ArticleUrl_;
import com.subitarius.domain.DistributedEntity;
import com.subitarius.domain.TagMapping;
import com.subitarius.domain.TagMapping_;
import com.subitarius.domain.Tag_;
import com.subitarius.util.logging.InjectLogger;

final class GetMappingsByTagHandler implements
		ActionHandler<GetMappingsByTag, GetMappingsResult> {
	@InjectLogger
	private Logger log;

	private final EntityManager entityManager;

	private final Mapper mapper;

	@Inject
	private GetMappingsByTagHandler(EntityManager entityManager, Mapper mapper) {
		this.entityManager = entityManager;
		this.mapper = mapper;
	}

	@Override
	public GetMappingsResult execute(final GetMappingsByTag action,
			Dispatcher dispatcher) throws ActionException {
		String tagName = action.getTagName();
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<TagMapping> criteria = builder
				.createQuery(TagMapping.class);
		Root<TagMapping> mappingRoot = criteria.from(TagMapping.class);
		criteria.where(builder.equal(
				mappingRoot.get(TagMapping_.tag).get(Tag_.name), tagName),
				builder.isNotEmpty(mappingRoot.get(TagMapping_.articleUrl).get(
						ArticleUrl_.articles)), builder.isEmpty(mappingRoot
						.<Set<DistributedEntity>> get("children")));
		List<TagMapping> mappings = entityManager.createQuery(criteria)
				.getResultList();

		// MAPPING hashes to articles
		final Map<String, Article> articles = Maps
				.newHashMapWithExpectedSize(mappings.size());
		for (TagMapping mapping : mappings) {
			Article article = mapping.getArticleUrl().getArticle();
			if (article != null) {
				articles.put(mapping.getHash(), article);
			}
		}

		List<TagMappingDto> dtos = Lists.transform(
				Lists.newArrayList(mappings),
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
					Article article1 = articles.get(m1.getHash());
					Article article2 = articles.get(m2.getHash());
					ArticleDto dto1 = mapper.map(article1, ArticleDto.class);
					ArticleDto dto2 = mapper.map(article2, ArticleDto.class);
					return action.getComparator().compare(dto1, dto2);
				}
			});
		}
		Collections2.filter(dtos, action);
		log.info("returned {} mappings for tag \"{}\"", dtos.size(), tagName);
		return new GetMappingsResult(dtos);
	}
}
