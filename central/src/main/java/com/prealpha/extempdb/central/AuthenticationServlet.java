/*
 * AuthenticationServlet.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.central;

import java.io.IOException;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.prealpha.extempdb.domain.User;
import com.prealpha.extempdb.domain.User_;

/**
 * Authenticates an HTTP client. Two methods are supported: {@code POST} allows
 * the client to submit its credentials, permitting access to secured servlets;
 * {@code DELETE} removes this access. See the documentation for each method for
 * more details. Clients are identified through {@link HttpSession}, which
 * typically requires the use of a cookie to store the session ID. To prevent
 * unauthorized access, the session ID should be kept secret.
 * <p>
 * 
 * <b>For {@code POST}, clients are required to send passwords in the clear. In
 * production, this servlet should be secured using TLS/SSL to prevent data
 * interception.</b>
 * 
 * @author Meyer Kizner
 * @see #doPost(HttpServletRequest, HttpServletResponse)
 * @see #doDelete(HttpServletRequest, HttpServletResponse)
 * 
 */
final class AuthenticationServlet extends HttpServlet {
	private final Provider<EntityManager> entityManagerProvider;

	@Inject
	private AuthenticationServlet(Provider<EntityManager> entityManagerProvider) {
		this.entityManagerProvider = entityManagerProvider;
	}

	/**
	 * Accepts a client's credentials and authorizes that client to access
	 * secured servlets. Three parameters are recognized: {@code version},
	 * {@code name}, and {@code password}. Each must appear exactly once with a
	 * non-empty value, otherwise status code 400 (bad request) is sent to the
	 * client.
	 * <p>
	 * 
	 * {@code version} is intended to allow a future server implementation to
	 * support multiple incompatible client protocols at once by identifying the
	 * version in use. At this time, the only acceptable value is the string
	 * {@code 0.2-alpha}; any other value will result in status code 501 (not
	 * implemented).
	 * <p>
	 * 
	 * {@code name} should be a valid username, and {@code password} should be
	 * the valid cleartext password corresponding to that username. If either is
	 * invalid, status code 403 (forbidden) is sent in response. An invalid
	 * username and an incorrect password are intentionally made
	 * indistinguishable to the client.
	 * <p>
	 * 
	 * If all parameters are valid and authentication succeeds, status code 200
	 * (OK) is returned along with an empty response body.
	 */
	/*
	 * TODO: maybe I'm thinking too hard about this, but if someone really
	 * wanted to know if a username were valid, they could use a timing attack.
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res)
			throws IOException, ServletException {
		@SuppressWarnings("unchecked")
		Map<String, String[]> params = req.getParameterMap();
		if (!checkParameters(params, "version", "name", "password")) {
			res.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		HttpSession session = req.getSession();

		String version = params.get("version")[0];
		if (!version.equals("0.2-alpha")) {
			res.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED);
			return;
		}

		String name = params.get("name")[0];
		User user = findUser(name);
		if (user != null) {
			String password = params.get("password")[0];
			if (user.authenticate(password)) {
				session.setAttribute(CentralModule.USER_ATTR, user);
				res.setStatus(HttpServletResponse.SC_OK);
				return;
			}
		}

		res.sendError(HttpServletResponse.SC_FORBIDDEN);
	}

	private User findUser(String name) {
		EntityManager entityManager = entityManagerProvider.get();
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<User> criteria = builder.createQuery(User.class);
		Root<User> userRoot = criteria.from(User.class);
		criteria.where(builder.equal(userRoot.get(User_.name), name));
		try {
			return entityManager.createQuery(criteria).getSingleResult();
		} catch (NoResultException nrx) {
			return null;
		}
	}

	private static boolean checkParameters(Map<String, String[]> params,
			String... keys) {
		for (String key : keys) {
			if (!params.containsKey(key)) {
				return false;
			} else if (params.get(key).length != 1) {
				return false;
			} else if (params.get(key)[0].isEmpty()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Removes a client's authorization to access secured servlets. All clients
	 * should make a request with this method once their use of secured servlets
	 * is complete. All parameters are ignored; similarly, if the client is not
	 * already authenticated, no action is taken. Any number of {@code DELETE}
	 * requests may be safely performed without adverse effects.
	 */
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse res)
			throws IOException, ServletException {
		HttpSession session = req.getSession(false);
		if (session != null) {
			session.invalidate();
		}
	}
}
