/*
 * GetParagraphsHandler.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.instance.server.action;

import java.util.List;

import javax.persistence.EntityManager;

import org.slf4j.Logger;

import com.google.inject.Inject;
import com.prealpha.xylophone.server.ActionHandler;
import com.prealpha.xylophone.shared.ActionException;
import com.subitarius.action.GetParagraphs;
import com.subitarius.action.GetParagraphsResult;
import com.subitarius.domain.Article;
import com.subitarius.util.logging.InjectLogger;

final class GetParagraphsHandler implements
		ActionHandler<GetParagraphs, GetParagraphsResult> {
	@InjectLogger
	private Logger log;

	private final EntityManager entityManager;

	@Inject
	private GetParagraphsHandler(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public GetParagraphsResult execute(GetParagraphs action)
			throws ActionException {
		String articleHash = action.getArticleHash();
		Article article = entityManager.find(Article.class, articleHash);
		List<String> paragraphs = article.getParagraphs();
		log.info("returned {} paragraphs for article hash: {}",
				paragraphs.size(), articleHash);
		return new GetParagraphsResult(paragraphs);
	}
}
