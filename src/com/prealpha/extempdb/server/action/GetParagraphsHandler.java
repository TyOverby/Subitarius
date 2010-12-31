/*
 * GetParagraphsHandler.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server.action;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;

import com.google.inject.Inject;
import com.prealpha.extempdb.server.InjectLogger;
import com.prealpha.extempdb.server.domain.Article;
import com.prealpha.extempdb.server.persistence.ArticleDao;
import com.prealpha.extempdb.server.persistence.Transactional;
import com.prealpha.extempdb.shared.action.GetParagraphs;
import com.prealpha.extempdb.shared.action.GetParagraphsResult;
import com.prealpha.extempdb.shared.dto.ArticleDto;
import com.prealpha.gwt.dispatch.server.ActionHandler;
import com.prealpha.gwt.dispatch.shared.ActionException;
import com.prealpha.gwt.dispatch.shared.Dispatcher;

class GetParagraphsHandler implements
		ActionHandler<GetParagraphs, GetParagraphsResult> {
	@InjectLogger
	private Logger log;

	private final ArticleDao articleDao;

	@Inject
	public GetParagraphsHandler(ArticleDao articleDao) {
		this.articleDao = articleDao;
	}

	@Override
	public Class<GetParagraphs> getActionType() {
		return GetParagraphs.class;
	}

	@Transactional
	@Override
	public GetParagraphsResult execute(GetParagraphs action,
			Dispatcher dispatcher) throws ActionException {
		ArticleDto dto = action.getArticle();
		Article article = articleDao.get(dto.getId());
		List<String> paragraphs = new ArrayList<String>(article.getParagraphs());

		log.info(
				"handled request for article paragraphs, article ID {}, returning {} paragraphs",
				article.getId(), paragraphs.size());

		return new GetParagraphsResult(paragraphs);
	}
}
