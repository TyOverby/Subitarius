/*
 * FetchEntitiesHandler.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.instance.server.action;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import com.prealpha.dispatch.server.ActionHandler;
import com.prealpha.dispatch.shared.ActionException;
import com.prealpha.dispatch.shared.Dispatcher;
import com.subitarius.action.FetchEntities;
import com.subitarius.action.MutationResult;
import com.subitarius.domain.DistributedEntity;
import com.subitarius.domain.DistributedEntity_;
import com.subitarius.instance.server.InstanceVersion;
import com.subitarius.util.http.RobotsExclusionException;
import com.subitarius.util.http.SimpleHttpClient;

class FetchEntitiesHandler implements
		ActionHandler<FetchEntities, MutationResult> {
	private static final String URL = "http://meyer.pre-alpha.com/DistributedEntity";

	private final EntityManager entityManager;

	private final SimpleHttpClient httpClient;

	private final String instanceVersion;

	@Inject
	private FetchEntitiesHandler(EntityManager entityManager,
			SimpleHttpClient httpClient, @InstanceVersion String instanceVersion) {
		this.entityManager = entityManager;
		this.httpClient = httpClient;
		this.instanceVersion = instanceVersion;
	}

	@Transactional
	@Override
	public MutationResult execute(FetchEntities action, Dispatcher dispatcher)
			throws ActionException {
		long timestamp = getTimestamp().getTime();
		ImmutableMap<String, String> params = ImmutableMap.of("version",
				instanceVersion, "timestamp", Long.toString(timestamp));
		try {
			InputStream stream = httpClient.doGet(URL, params);
			ObjectInputStream ois = new ObjectInputStream(stream);
			int entityCount = ois.readInt();
			for (int i = 0; i < entityCount; i++) {
				try {
					DistributedEntity entity = (DistributedEntity) ois
							.readObject();
					entityManager.persist(entity);
				} catch (ClassCastException ccx) {
					throw new ActionException(ccx);
				} catch (ClassNotFoundException cnfx) {
					throw new ActionException(cnfx);
				} catch (ObjectStreamException osx) {
					throw new ActionException(osx);
				}
			}
			ois.close();
			stream.close();
		} catch (IOException iox) {
			throw new ActionException(iox);
		} catch (RobotsExclusionException rex) {
			throw new ActionException(rex);
		}
		return MutationResult.SUCCESS;
	}

	/**
	 * Returns the timestamp which should be used to limit returned entities.
	 * This is the persist date of the most recent entity in the database.
	 * <p>
	 * 
	 * This method should only be called within a database transaction.
	 * 
	 * @return the limiting timestamp
	 */
	/*
	 * TODO: what if a user creates an entity at some point and we miss
	 * something? We're going to need to store this value somehow.
	 */
	private Date getTimestamp() {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<DistributedEntity> criteria = builder
				.createQuery(DistributedEntity.class);
		Root<DistributedEntity> root = criteria.from(DistributedEntity.class);
		criteria.orderBy(builder.desc(root.get(DistributedEntity_.persistDate)));
		try {
			DistributedEntity entity = entityManager.createQuery(criteria)
					.setMaxResults(1).getSingleResult();
			return entity.getPersistDate();
		} catch (NoResultException nrx) {
			return new Date(0L);
		}
	}
}
