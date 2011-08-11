/*
 * AddMappingHandler.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.server.action;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.dozer.Mapper;
import org.slf4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import com.prealpha.dispatch.server.ActionHandler;
import com.prealpha.dispatch.shared.ActionException;
import com.prealpha.dispatch.shared.Dispatcher;
import com.prealpha.extempdb.domain.ArticleUrl;
import com.prealpha.extempdb.domain.Tag;
import com.prealpha.extempdb.domain.TagMapping;
import com.prealpha.extempdb.domain.TagMapping.State;
import com.prealpha.extempdb.domain.TagMapping_;
import com.prealpha.extempdb.domain.Tag_;
import com.prealpha.extempdb.domain.Team;
import com.prealpha.extempdb.instance.shared.action.AddMapping;
import com.prealpha.extempdb.instance.shared.action.MutationResult;
import com.prealpha.extempdb.util.logging.InjectLogger;

class AddMappingHandler implements ActionHandler<AddMapping, MutationResult> {
	@InjectLogger
	private Logger log;

	private final EntityManager entityManager;

	private final Mapper mapper;

	private final Provider<Team> teamProvider;

	@Inject
	public AddMappingHandler(EntityManager entityManager, Mapper mapper,
			Provider<Team> teamProvider) {
		this.entityManager = entityManager;
		this.mapper = mapper;
		this.teamProvider = teamProvider;
	}

	@Transactional
	@Override
	public MutationResult execute(AddMapping action, Dispatcher dispatcher)
			throws ActionException {
		String tagName = action.getTagName();
		String articleUrlHash = action.getArticleUrlHash();
		State state = mapper.map(action.getState(), State.class);

		Tag tag = getTag(tagName);
		ArticleUrl articleUrl = entityManager.find(ArticleUrl.class,
				articleUrlHash);
		if (tag == null) {
			log.info("rejected request to map invalid tag: {}", tagName);
			return MutationResult.INVALID_REQUEST;
		} else if (articleUrl == null) {
			log.info("rejected request to map invalid URL hash: {}",
					articleUrlHash);
			return MutationResult.INVALID_REQUEST;
		} else {
			TagMapping parent = getMapping(tag, articleUrl);
			TagMapping mapping = new TagMapping(teamProvider.get(), parent,
					tag, articleUrl, state);
			entityManager.persist(mapping);
			log.info("created or updated tag mapping: {}", mapping);
			return MutationResult.SUCCESS;
		}
	}

	private Tag getTag(String tagName) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Tag> criteria = builder.createQuery(Tag.class);
		Root<Tag> tagRoot = criteria.from(Tag.class);
		criteria.where(builder.equal(builder.upper(tagRoot.get(Tag_.name)),
				tagName.toUpperCase()));
		try {
			return entityManager.createQuery(criteria).getSingleResult();
		} catch (NoResultException nrx) {
			return null;
		}
	}

	private TagMapping getMapping(Tag tag, ArticleUrl articleUrl) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<TagMapping> criteria = builder
				.createQuery(TagMapping.class);
		Root<TagMapping> mappingRoot = criteria.from(TagMapping.class);
		criteria.where(builder.equal(mappingRoot.get(TagMapping_.tag), tag));
		criteria.where(builder.equal(mappingRoot.get(TagMapping_.articleUrl),
				articleUrl));
		try {
			return entityManager.createQuery(criteria).getSingleResult();
		} catch (NoResultException nrx) {
			return null;
		}
	}
}
