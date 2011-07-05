/*
 * AddMappingHandler.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server.action;

import java.util.ArrayList;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import com.prealpha.dispatch.server.ActionHandler;
import com.prealpha.dispatch.shared.ActionException;
import com.prealpha.dispatch.shared.Dispatcher;
import com.prealpha.extempdb.domain.Article;
import com.prealpha.extempdb.domain.Tag;
import com.prealpha.extempdb.domain.TagMapping;
import com.prealpha.extempdb.domain.TagMapping.State;
import com.prealpha.extempdb.domain.TagMappingAction;
import com.prealpha.extempdb.domain.User;
import com.prealpha.extempdb.server.InjectLogger;
import com.prealpha.extempdb.shared.action.AddMapping;
import com.prealpha.extempdb.shared.action.MutationResult;

class AddMappingHandler implements ActionHandler<AddMapping, MutationResult> {
	@InjectLogger
	private Logger log;

	private final EntityManager entityManager;

	private final HttpSession httpSession;

	@Inject
	public AddMappingHandler(EntityManager entityManager,
			HttpSession httpSession) {
		this.entityManager = entityManager;
		this.httpSession = httpSession;
	}

	@Transactional
	@Override
	public MutationResult execute(AddMapping action, Dispatcher dispatcher)
			throws ActionException {
		User user = (User) httpSession.getAttribute("user");
		String tagName = action.getTagName();
		Long articleId = action.getArticleId();

		if (user == null) {
			log.info(
					"denied permission to map tag \"{}\" to article ID {} (not logged in)",
					tagName, articleId);
			return MutationResult.PERMISSION_DENIED;
		}

		TagMapping.Key key = new TagMapping.Key(tagName, articleId);
		TagMapping mapping = entityManager.find(TagMapping.class, key);

		if (mapping == null) {
			Tag tag = entityManager.find(Tag.class, tagName);
			Article article = entityManager.find(Article.class, articleId);

			if (tag == null || article == null) {
				log.info(
						"rejected mapping attempt; invalid tag (\"{}\") or article ({})",
						tagName, articleId);
				return MutationResult.INVALID_REQUEST;
			}

			mapping = new TagMapping();
			mapping.setKey(key);
			mapping.setTag(tag);
			mapping.setArticle(article);
			mapping.setAdded(new Date());
			mapping.setActions(new ArrayList<TagMappingAction>());
			entityManager.persist(mapping);
		}

		if (!mapping.getState().equals(State.PATROLLED)) {
			TagMappingAction mappingAction = new TagMappingAction();
			mappingAction.setMapping(mapping);
			mappingAction.setType(TagMappingAction.Type.PATROL);
			mappingAction.setUser(user);
			mappingAction.setTimestamp(new Date());
			entityManager.persist(mappingAction);
		}

		log.info("user \"{}\" mapped tag \"{}\" to article ID {}",
				new Object[] { user.getName(), tagName, articleId });
		return MutationResult.SUCCESS;
	}
}
