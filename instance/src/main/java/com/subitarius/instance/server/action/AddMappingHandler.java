/*
 * AddMappingHandler.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.instance.server.action;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import com.prealpha.dispatch.server.ActionHandler;
import com.prealpha.dispatch.shared.ActionException;
import com.prealpha.dispatch.shared.Dispatcher;
import com.subitarius.action.AddMapping;
import com.subitarius.action.MutationResult;
import com.subitarius.domain.ArticleUrl;
import com.subitarius.domain.Tag;
import com.subitarius.domain.TagMapping;
import com.subitarius.domain.TagMapping.State;
import com.subitarius.domain.TagMapping_;
import com.subitarius.domain.Tag_;
import com.subitarius.domain.Team;
import com.subitarius.util.logging.InjectLogger;

class AddMappingHandler implements ActionHandler<AddMapping, MutationResult> {
	@InjectLogger
	private Logger log;

	private final EntityManager entityManager;

	private final Provider<Team> teamProvider;

	@Inject
	AddMappingHandler(EntityManager entityManager, Provider<Team> teamProvider) {
		this.entityManager = entityManager;
		this.teamProvider = teamProvider;
	}

	@Transactional
	@Override
	public MutationResult execute(AddMapping action, Dispatcher dispatcher)
			throws ActionException {
		String tagName = action.getTagName();
		String articleUrlHash = action.getArticleUrlHash();
		State state = State.valueOf(action.getState().name());

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
		criteria.where(builder.and(builder.equal(
				mappingRoot.get(TagMapping_.tag), tag), builder.equal(
				mappingRoot.get(TagMapping_.articleUrl), articleUrl)));
		try {
			return entityManager.createQuery(criteria).getSingleResult();
		} catch (NoResultException nrx) {
			return null;
		}
	}
}
