/*
 * GetParagraphsHandler.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.server.action;

import java.util.List;

import javax.persistence.EntityManager;

import org.slf4j.Logger;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import com.prealpha.dispatch.server.ActionHandler;
import com.prealpha.dispatch.shared.ActionException;
import com.prealpha.dispatch.shared.Dispatcher;
import com.prealpha.extempdb.domain.Article;
import com.prealpha.extempdb.instance.shared.action.GetParagraphs;
import com.prealpha.extempdb.instance.shared.action.GetParagraphsResult;
import com.prealpha.extempdb.util.logging.InjectLogger;

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
		String articleHash = action.getArticleHash();
		Article article = entityManager.find(Article.class, articleHash);
		List<String> paragraphs = article.getParagraphs();
		log.info("returned {} paragraphs for article hash: {}",
				paragraphs.size(), articleHash);
		return new GetParagraphsResult(paragraphs);
	}
}
