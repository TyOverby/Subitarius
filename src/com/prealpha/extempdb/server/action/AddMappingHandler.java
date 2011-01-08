/*
 * AddMappingHandler.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server.action;

import java.util.Date;

import org.slf4j.Logger;

import com.google.inject.Inject;
import com.prealpha.dispatch.server.ActionHandler;
import com.prealpha.dispatch.shared.ActionException;
import com.prealpha.dispatch.shared.Dispatcher;
import com.prealpha.extempdb.server.InjectLogger;
import com.prealpha.extempdb.server.domain.Article;
import com.prealpha.extempdb.server.domain.Tag;
import com.prealpha.extempdb.server.domain.TagMapping;
import com.prealpha.extempdb.server.domain.TagMappingAction;
import com.prealpha.extempdb.server.domain.User;
import com.prealpha.extempdb.server.persistence.ArticleDao;
import com.prealpha.extempdb.server.persistence.TagDao;
import com.prealpha.extempdb.server.persistence.TagMappingDao;
import com.prealpha.extempdb.server.persistence.TagMappingActionDao;
import com.prealpha.extempdb.server.persistence.Transactional;
import com.prealpha.extempdb.server.persistence.UserSessionDao;
import com.prealpha.extempdb.shared.action.AddMapping;
import com.prealpha.extempdb.shared.action.MutationResult;
import com.prealpha.extempdb.shared.dto.ArticleDto;
import com.prealpha.extempdb.shared.dto.TagDto;
import com.prealpha.extempdb.shared.id.UserSessionToken;

class AddMappingHandler implements ActionHandler<AddMapping, MutationResult> {
	@InjectLogger
	private Logger log;
	
	private final TagDao tagDao;
	
	private final ArticleDao articleDao;
	
	private final TagMappingDao tagMappingDao;
	
	private final TagMappingActionDao tagMappingActionDao;
	
	private final UserSessionDao userSessionDao;
	
	@Inject
	public AddMappingHandler(TagDao tagDao, ArticleDao articleDao, TagMappingDao tagMappingDao, TagMappingActionDao tagMappingActionDao, UserSessionDao userSessionDao) {
		this.tagDao = tagDao;
		this.articleDao = articleDao;
		this.tagMappingDao = tagMappingDao;
		this.tagMappingActionDao = tagMappingActionDao;
		this.userSessionDao = userSessionDao;
	}
	
	@Transactional
	@Override
	public MutationResult execute(AddMapping action, Dispatcher dispatcher) throws ActionException {
		UserSessionToken sessionToken = action.getSessionToken();
		UserSession session = userSessionDao.validateSession(sessionToken);
		
		TagDto tagDto = action.getTag();
		ArticleDto articleDto = action.getArticle();
		
		if (session == null) {
			log.info("rejected attempt to map tag \"{}\" to article ID {} due to invalid session", tagDto.getName(), articleDto.getId());
			return MutationResult.INVALID_SESSION;
		}
		
		Tag tag = tagDao.get(tagDto.getName());
		Article article = articleDao.get(articleDto.getId());
		User user = session.getUser();
		
		if (tagMappingDao.get(tag, article) != null) {
			log.info("rejected attempt by user \"{}\" to remap tag \"{}\" to article ID {}", user.getName(), tag.getName(), article.getId());
			return MutationResult.INVALID_REQUEST;
		}
		
		TagMapping mapping = new TagMapping();
		mapping.setTag(tag);
		mapping.setArticle(article);
		mapping.setAdded(new Date());
		tagMappingDao.save(mapping);
		
		TagMappingAction mappingAction = new TagMappingAction();
		mappingAction.setMapping(mapping);
		mappingAction.setType(TagMappingAction.Type.PATROL);
		mappingAction.setUser(user);
		mappingAction.setTimestamp(new Date());
		tagMappingActionDao.save(mappingAction);
		
		log.info("user \"{}\" mapped tag \"{}\" to article ID {}", user.getName(), tag.getName(), article.getId());
		return MutationResult.SUCCESS;
	}
}
