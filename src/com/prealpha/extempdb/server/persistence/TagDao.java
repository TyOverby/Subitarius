/*
 * TagDao.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server.persistence;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.StandardBasicTypes;

import com.prealpha.extempdb.server.domain.Tag;

public class TagDao extends GenericDao<Tag, String> {
	@Override
	protected Class<Tag> getEntityClass() {
		return Tag.class;
	}

	@SuppressWarnings("unchecked")
	public Iterable<Tag> getRoots() {
		Session session = sessionProvider.get();
		Criteria criteria = session.createCriteria(getEntityClass());
		criteria.add(Restrictions.isEmpty("parents"));
		return criteria.list();
	}

	/*
	 * TODO: this method isn't compatible with other DBMS
	 */
	@SuppressWarnings("unchecked")
	public Iterable<Tag> getByNamePrefix(String namePrefix) {
		final String regex = "[[:<:]]" + namePrefix + ".*";

		Session session = sessionProvider.get();
		Criteria criteria = session.createCriteria(getEntityClass());
		criteria.add(Restrictions.sqlRestriction("name REGEXP ?", regex,
				StandardBasicTypes.STRING));
		return criteria.list();
	}
}
