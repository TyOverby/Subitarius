/*
 * PersistenceModule.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server.persistence;

import java.util.Random;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.uncommons.maths.random.AESCounterRNG;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.matcher.Matchers;

public class PersistenceModule extends AbstractModule {
	public PersistenceModule() {
	}

	@Override
	protected void configure() {
		bind(Random.class).to(AESCounterRNG.class);

		TransactionalInterceptor transactionalInterceptor = new TransactionalInterceptor();
		requestInjection(transactionalInterceptor);
		bindInterceptor(Matchers.any(),
				Matchers.annotatedWith(Transactional.class),
				transactionalInterceptor);
	}

	@Provides
	Configuration getConfiguration() {
		return new Configuration().configure();
	}

	@Provides
	@Singleton
	@Inject
	SessionFactory getSessionFactory(Configuration configuration) {
		return configuration.buildSessionFactory();
	}

	@Provides
	@Inject
	Session getSession(SessionFactory sessionFactory) {
		return sessionFactory.getCurrentSession();
	}
}
