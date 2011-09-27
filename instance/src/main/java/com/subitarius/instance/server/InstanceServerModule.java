/*
 * InstanceServerModule.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.instance.server;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.matcher.Matchers;
import com.google.inject.persist.PersistFilter;
import com.google.inject.persist.Transactional;
import com.google.inject.servlet.RequestScoped;
import com.google.inject.servlet.ServletModule;
import com.subitarius.domain.Team;
import com.subitarius.util.logging.Slf4jTypeListener;

public class InstanceServerModule extends ServletModule {
	public InstanceServerModule() {
	}

	@Override
	protected void configureServlets() {
		filter("/*").through(PersistFilter.class);

		bind(GuiceServiceServlet.class).in(Singleton.class);
		serve("/GWT.rpc").with(GuiceServiceServlet.class);

		bind(ActionServlet.class).in(Singleton.class);
		serve("/java.io").with(ActionServlet.class);

		bindListener(Matchers.any(), new Slf4jTypeListener());

		// this is for breaking changes only, so 0.2.1 doesn't count
		bindConstant().annotatedWith(InstanceVersion.class).to("0.2-alpha");
	}

	@Provides
	@RequestScoped
	@Transactional
	@Inject
	Team getTeam(EntityManager entityManager) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Team> criteria = builder.createQuery(Team.class);
		criteria.select(criteria.from(Team.class));
		criteria.distinct(true);
		return entityManager.createQuery(criteria).getSingleResult();
	}
}
