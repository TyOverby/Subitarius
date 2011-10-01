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
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import com.prealpha.dispatch.server.ActionHandler;
import com.prealpha.dispatch.shared.ActionException;
import com.prealpha.dispatch.shared.Dispatcher;
import com.subitarius.action.FetchEntities;
import com.subitarius.action.MutationResult;
import com.subitarius.domain.DistributedEntity;
import com.subitarius.domain.DistributedEntity_;
import com.subitarius.domain.Tag;
import com.subitarius.domain.TagMapping;
import com.subitarius.instance.server.InstanceVersion;
import com.subitarius.util.http.RobotsExclusionException;
import com.subitarius.util.http.SimpleHttpClient;
import com.subitarius.util.logging.InjectLogger;

class FetchEntitiesHandler implements
		ActionHandler<FetchEntities, MutationResult> {
	private static final String URL = "http://meyer.pre-alpha.com/DistributedEntity";

	@InjectLogger
	private Logger log;

	private final EntityManager entityManager;

	private final SimpleHttpClient httpClient;

	private final String instanceVersion;

	@Inject
	FetchEntitiesHandler(EntityManager entityManager,
			SimpleHttpClient httpClient, @InstanceVersion String instanceVersion) {
		this.entityManager = entityManager;
		this.httpClient = httpClient;
		this.instanceVersion = instanceVersion;
	}

	@Override
	public MutationResult execute(FetchEntities action, Dispatcher dispatcher)
			throws ActionException {
		long timestamp = getTimestamp().getTime();
		ImmutableMap<String, String> params = ImmutableMap.of("version",
				instanceVersion, "timestamp", Long.toString(timestamp));
		Set<Tag> tags = Sets.newHashSet();
		Set<DistributedEntity> entities = Sets.newHashSet();
		try {
			InputStream stream = httpClient.doGet(URL, params);
			ObjectInputStream ois = new ObjectInputStream(stream);
			int entityCount = ois.readInt();
			log.debug("received {} entities from server", entityCount);
			for (int i = 0; i < entityCount; i++) {
				try {
					DistributedEntity entity = (DistributedEntity) ois
							.readObject();
					entities.add(entity);
					log.trace("received entity: {}", entity);
					if (entity instanceof TagMapping) {
						Tag child = ((TagMapping) entity).getTag();
						tags.addAll(getAllAncestors(child));
						log.trace("received tag: {}", child);
					}
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
		flushTags(tags);
		flushEntities(entities);
		log.debug("fetch complete");
		return MutationResult.SUCCESS;
	}

	/**
	 * Returns the timestamp which should be used to limit returned entities.
	 * This is the persist date of the most recent entity in the database.
	 * 
	 * @return the limiting timestamp
	 */
	/*
	 * XXX: what if a user creates an entity at some point and we miss
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

	private static Set<Tag> getAllAncestors(Tag tag) {
		Set<Tag> ancestors = Sets.newHashSet();
		ancestors.add(tag);
		for (Tag parent : tag.getParents()) {
			ancestors.addAll(getAllAncestors(parent));
		}
		return ancestors;
	}

	@Transactional
	void flushTags(Set<Tag> tags) {
		log.trace("flushing {} tags", tags.size());

		// find the set of roots, which we persist first
		Set<Tag> ancestors = Sets.newHashSet();
		for (Tag tag : tags) {
			if (tag.getParents().isEmpty()) {
				entityManager.merge(tag);
				ancestors.add(tag);
			}
		}

		// persist all other tags such that the parents are persisted first
		int remainingCount;
		do {
			remainingCount = 0;
			Set<Tag> toAdd = Sets.newHashSet();
			for (Tag tag : tags) {
				Set<Tag> parents = tag.getParents();
				if (!ancestors.contains(tag)) {
					if (ancestors.containsAll(parents)) {
						entityManager.merge(tag);
						toAdd.add(tag);
					} else {
						remainingCount++;
					}
				}
			}
			ancestors.addAll(toAdd);
		} while (remainingCount > 0);
	}

	@Transactional
	void flushEntities(Set<DistributedEntity> entities) {
		log.trace("flushing {} entities", entities.size());
		// perform two passes so that all references between entities are made
		// in the first pass, we get URLs only; in the second, we get all
		for (DistributedEntity entity : entities) {
			if (!(entity instanceof TagMapping)) {
				entityManager.merge(entity);
			}
		}
		for (DistributedEntity entity : entities) {
			entityManager.merge(entity);
		}
	}
}
