/*
 * DistributedEntityServlet.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.central;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import com.subitarius.domain.DistributedEntity;
import com.subitarius.domain.DistributedEntity_;
import com.subitarius.util.logging.InjectLogger;

/**
 * Provides access to {@link DistributedEntity} objects stored on the server.
 * The only supported method is {@code GET}.
 * 
 * @author Meyer Kizner
 * @see #doGet(HttpServletRequest, HttpServletResponse)
 * 
 */
class DistributedEntityServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@InjectLogger
	private Logger log;

	private final Provider<EntityManager> entityManagerProvider;

	@Inject
	private DistributedEntityServlet(
			Provider<EntityManager> entityManagerProvider) {
		this.entityManagerProvider = entityManagerProvider;
	}

	/**
	 * Fetches and returns the serialized forms of any distributed entities
	 * matching the criteria specified in the request parameters. One optional
	 * parameter is supported to limit results: {@code timestamp} is interpreted
	 * as a {@code long} representing some number of milliseconds past the
	 * epoch; if this parameter is used, results will be limited to entities
	 * created at or after this time.
	 * <p>
	 * 
	 * Additionally, there is a single required parameter, {@code version},
	 * which is intended to allow for future support of multiple protocol
	 * versions. At present, only a single {@code version} value is allowed,
	 * {@code 0.2-alpha}. Any other value will result in status code 501 (not
	 * implemented).
	 * <p>
	 * 
	 * If {@code timestamp} is present and cannot be parsed as a {@code long},
	 * status code 400 (bad request) will be returned. Otherwise, the result
	 * will consist of a byte stream readable by
	 * {@link java.io.ObjectInputStream}. First in the stream will be an
	 * {@code int} indicating the number of serialized entities. This will be
	 * followed by the serialized forms of the entities in arbitrary order.
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res)
			throws IOException, ServletException {
		String version = req.getParameter("version");
		if (!"0.2-alpha".equals(version)) {
			log.info("rejected request with unknown version: {}", version);
			res.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED);
			return;
		}

		String timestampStr = req.getParameter("timestamp");
		Date timestamp;
		if (timestampStr != null) {
			try {
				timestamp = new Date(Long.parseLong(timestampStr));
			} catch (NumberFormatException nfx) {
				log.info("rejected request with invalid timestamp: {}",
						timestampStr);
				res.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
		} else {
			timestamp = null;
		}

		EntityManager entityManager = entityManagerProvider.get();
		List<DistributedEntity> entities = getEntities(timestamp);
		log.debug("sending {} entities in response to query", entities.size());
		res.setContentType("application/x-java-serialized-object");
		ObjectOutputStream oos = new ObjectOutputStream(res.getOutputStream());
		oos.writeInt(entities.size());
		for (DistributedEntity entity : entities) {
			entityManager.detach(entity);
			oos.writeObject(entity);
		}
		oos.flush();
		oos.close();
	}

	/**
	 * Returns entities persisted at or before the specified timestamp. The
	 * parameter is optional and may be {@code null} to indicate that no
	 * restriction should apply.
	 * 
	 * @param timestamp
	 *            the earliest timestamp for which entities should be returned
	 * @return a list of entities persisted at or after the timestamp
	 */
	@Transactional
	private List<DistributedEntity> getEntities(Date timestamp) {
		EntityManager entityManager = entityManagerProvider.get();
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<DistributedEntity> criteria = builder
				.createQuery(DistributedEntity.class);
		Root<DistributedEntity> root = criteria.from(DistributedEntity.class);
		criteria.select(root);
		if (timestamp != null) {
			criteria.where(builder.greaterThanOrEqualTo(
					root.get(DistributedEntity_.persistDate), timestamp));
		}
		return entityManager.createQuery(criteria).getResultList();
	}
}
