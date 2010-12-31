/*
 * TagMappingDao.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server.persistence;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.prealpha.extempdb.server.domain.Article;
import com.prealpha.extempdb.server.domain.Tag;
import com.prealpha.extempdb.server.domain.TagMapping;

public class TagMappingDao extends GenericDao<TagMapping, Long> {
	@Override
	public Class<TagMapping> getEntityClass() {
		return TagMapping.class;
	}

	public TagMapping get(Tag tag, Article article) {
		Session session = sessionProvider.get();
		Criteria criteria = session.createCriteria(TagMapping.class);
		criteria.add(Restrictions.eq("tag", tag));
		criteria.add(Restrictions.eq("article", article));
		return (TagMapping) criteria.uniqueResult();
	}
}
