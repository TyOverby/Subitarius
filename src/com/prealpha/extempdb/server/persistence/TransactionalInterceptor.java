/*
 * TransactionalInterceptor.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server.persistence;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.context.ManagedSessionContext;

import com.google.inject.Inject;

class TransactionalInterceptor implements MethodInterceptor {
	@Inject
	private SessionFactory sessionFactory;

	public TransactionalInterceptor() {
	}

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		Session session = sessionFactory.openSession();
		ManagedSessionContext.bind((org.hibernate.classic.Session) session);

		Transaction transaction = session.beginTransaction();

		try {
			Object result = invocation.proceed();
			transaction.commit();
			return result;
		} catch (Throwable thrown) {
			transaction.rollback();
			throw thrown;
		} finally {
			session.close();
			ManagedSessionContext.unbind(sessionFactory);
		}
	}
}
