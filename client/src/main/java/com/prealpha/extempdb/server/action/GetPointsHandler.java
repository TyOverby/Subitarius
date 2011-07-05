/*
 * GetPointsHandler.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.dozer.Mapper;
import org.slf4j.Logger;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import com.prealpha.dispatch.server.ActionHandler;
import com.prealpha.dispatch.shared.ActionException;
import com.prealpha.dispatch.shared.Dispatcher;
import com.prealpha.extempdb.domain.TagMapping;
import com.prealpha.extempdb.domain.TagMappingAction;
import com.prealpha.extempdb.domain.TagMapping_;
import com.prealpha.extempdb.domain.User;
import com.prealpha.extempdb.server.InjectLogger;
import com.prealpha.extempdb.shared.action.GetPoints;
import com.prealpha.extempdb.shared.action.GetPointsResult;
import com.prealpha.extempdb.shared.dto.UserDto;

class GetPointsHandler implements ActionHandler<GetPoints, GetPointsResult> {
	@InjectLogger
	private Logger log;

	private final EntityManager entityManager;

	private final Mapper mapper;

	@Inject
	public GetPointsHandler(EntityManager entityManager, Mapper mapper) {
		this.entityManager = entityManager;
		this.mapper = mapper;
	}

	@Transactional
	@Override
	public GetPointsResult execute(GetPoints action, Dispatcher dispatcher)
			throws ActionException {
		Map<User, Integer> points = new HashMap<User, Integer>();

		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<TagMapping> criteria = builder
				.createQuery(TagMapping.class);
		Root<TagMapping> tagMappingRoot = criteria.from(TagMapping.class);
		criteria.where(builder.isNotEmpty(tagMappingRoot
				.get(TagMapping_.actions)));
		List<TagMapping> mappings = entityManager.createQuery(criteria)
				.getResultList();

		for (TagMapping mapping : mappings) {
			TagMappingAction lastAction = mapping.getLastAction();
			User user = lastAction.getUser();

			if (points.containsKey(user)) {
				points.put(user, points.get(user) + 1);
			} else {
				points.put(user, 1);
			}
		}

		Map<UserDto, Integer> pointsResult = new HashMap<UserDto, Integer>();
		for (Map.Entry<User, Integer> entry : points.entrySet()) {
			UserDto dto = mapper.map(entry.getKey(), UserDto.class);
			pointsResult.put(dto, entry.getValue());
		}

		log.info("returned points data on request");
		return new GetPointsResult(pointsResult);
	}
}
