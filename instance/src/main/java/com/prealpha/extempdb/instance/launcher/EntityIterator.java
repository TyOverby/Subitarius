/*
 * EntityIterator.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.launcher;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.UnmodifiableIterator;
import com.google.inject.Inject;
import com.prealpha.extempdb.domain.DistributedEntity;
import com.prealpha.extempdb.domain.DistributedEntity_;

final class EntityIterator extends UnmodifiableIterator<DistributedEntity> {
	private final EntityManager entityManager;

	private Date limit;

	private List<DistributedEntity> cache;

	private int index;

	@Inject
	private EntityIterator(EntityManager entityManager) {
		this.entityManager = entityManager;
		limit = new Date();
		cache = ImmutableList.of();
		index = 0;
	}

	@Override
	public boolean hasNext() {
		updateCache();
		return !cache.isEmpty();
	}

	@Override
	public DistributedEntity next() {
		if (cache.isEmpty()) {
			throw new NoSuchElementException();
		}
		DistributedEntity next = cache.get(index++);
		limit = new Date(next.getPersistDate().getTime() + 1);
		entityManager.detach(next);
		return next;
	}

	private void updateCache() {
		if (cache.isEmpty()) {
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<DistributedEntity> criteria = builder
					.createQuery(DistributedEntity.class);
			Root<DistributedEntity> root = criteria
					.from(DistributedEntity.class);
			Expression<Date> persistDate = root
					.get(DistributedEntity_.persistDate);
			criteria.where(builder.greaterThanOrEqualTo(persistDate, limit));
			cache = entityManager.createQuery(criteria).getResultList();
			index = 0;
		}
	}
}
