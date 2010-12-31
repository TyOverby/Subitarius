/*
 * GenericDao.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server.persistence;

import java.io.Serializable;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.google.inject.Inject;
import com.google.inject.Provider;

abstract class GenericDao<T, Id extends Serializable> {
	@Inject
	protected Provider<Session> sessionProvider;

	public GenericDao() {
	}

	protected abstract Class<T> getEntityClass();

	@SuppressWarnings("unchecked")
	public Iterable<T> getAll() {
		return sessionProvider.get().createCriteria(getEntityClass()).list();
	}

	@SuppressWarnings("unchecked")
	public T get(Id id) {
		Session session = sessionProvider.get();
		Criteria criteria = session.createCriteria(getEntityClass()).add(
				Restrictions.idEq(id));
		return (T) criteria.uniqueResult();
	}

	public void save(T... entities) {
		Session session = sessionProvider.get();

		for (T entity : entities) {
			session.saveOrUpdate(entity);
		}

		session.flush();
	}

	public void delete(T... entities) {
		Session session = sessionProvider.get();

		for (T entity : entities) {
			session.delete(entity);
		}

		session.flush();
	}
}
