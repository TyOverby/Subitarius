/*
 * ArticleDao.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server.persistence;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.prealpha.extempdb.server.domain.Article;

public class ArticleDao extends GenericDao<Article, Long> {
	@Override
	protected Class<Article> getEntityClass() {
		return Article.class;
	}

	public Article getByUrl(String url) {
		Session session = sessionProvider.get();
		Criteria criteria = session.createCriteria(getEntityClass()).add(
				Restrictions.eq("url", url));
		Article article = (Article) criteria.uniqueResult();
		return article;
	}
}
