/*
 * GetParagraphsHandler.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server.action;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.slf4j.Logger;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import com.prealpha.dispatch.server.ActionHandler;
import com.prealpha.dispatch.shared.ActionException;
import com.prealpha.dispatch.shared.Dispatcher;
import com.prealpha.extempdb.domain.Article;
import com.prealpha.extempdb.server.InjectLogger;
import com.prealpha.extempdb.shared.action.GetParagraphs;
import com.prealpha.extempdb.shared.action.GetParagraphsResult;

class GetParagraphsHandler implements
		ActionHandler<GetParagraphs, GetParagraphsResult> {
	@InjectLogger
	private Logger log;

	private final EntityManager entityManager;

	@Inject
	public GetParagraphsHandler(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Transactional
	@Override
	public GetParagraphsResult execute(GetParagraphs action,
			Dispatcher dispatcher) throws ActionException {
		Long articleId = action.getArticleId();
		Article article = entityManager.find(Article.class, articleId);
		List<String> paragraphs = new ArrayList<String>(article.getParagraphs());

		log.info(
				"handled request for article paragraphs, article ID {}, returning {} paragraphs",
				article.getId(), paragraphs.size());

		return new GetParagraphsResult(paragraphs);
	}
}
