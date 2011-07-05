/*
 * SessionValidator.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server.action;

import javax.servlet.http.HttpSession;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.prealpha.extempdb.shared.action.AuthenticatedAction;
import com.prealpha.extempdb.shared.action.InvalidSessionException;

final class SessionValidator implements MethodInterceptor {
	@Inject
	private Provider<HttpSession> sessionProvider;

	public SessionValidator() {
	}

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		HttpSession httpSession = sessionProvider.get();
		Object[] arguments = invocation.getArguments();

		for (Object obj : arguments) {
			if (obj instanceof AuthenticatedAction<?>) {
				AuthenticatedAction<?> action = (AuthenticatedAction<?>) arguments[0];

				if (!httpSession.getId().equals(action.getSessionId())) {
					httpSession.invalidate();
					throw new InvalidSessionException(httpSession.getId(),
							action.getSessionId());
				}
			}
		}

		return invocation.proceed();
	}
}
