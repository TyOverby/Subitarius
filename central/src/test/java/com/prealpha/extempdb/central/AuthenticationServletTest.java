/*
 * AuthenticationServletTest.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.central;

import static org.easymock.EasyMock.*;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Date;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.easymock.Mock;
import com.mycila.testing.plugin.guice.Bind;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;
import com.prealpha.extempdb.domain.Team;
import com.prealpha.extempdb.domain.User;
import com.prealpha.extempdb.util.logging.TestLoggingModule;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ TestLoggingModule.class })
public final class AuthenticationServletTest {
	@SuppressWarnings("unused")
	@ModuleProvider
	private static Iterable<Module> getModules() {
		return ImmutableList.<Module> of(new JpaPersistModule("central-test"),
				new AbstractModule() {
					@Override
					protected void configure() {
						requestStaticInjection(Team.class);
						requestStaticInjection(User.class);
					}
				});
	}

	@Inject
	private AuthenticationServlet servlet;

	@Inject
	private PersistService persistService;

	@Inject
	private Provider<EntityManager> entityManagerProvider;

	@Mock(Mock.Type.NICE)
	@Bind
	private Signature algorithm;

	@Mock(Mock.Type.NICE)
	private PrivateKey privateKey;

	@Mock(Mock.Type.NICE)
	private HttpServletRequest req;

	@Mock(Mock.Type.STANDARD)
	private HttpServletResponse res;

	@Mock(Mock.Type.NICE)
	private HttpSession session;

	@Before
	public void setUp() throws SignatureException, InvalidKeyException {
		algorithm.initSign(privateKey);
		expectLastCall().anyTimes();
		expect(algorithm.sign()).andReturn(new byte[] {}).anyTimes();

		replay(algorithm);
		persistService.start();
		EntityManager entityManager = entityManagerProvider.get();
		entityManager.getTransaction().begin();
		Team team = new Team("test team", new Date(Long.MAX_VALUE), 1,
				privateKey);
		User user = new User("testuser", "password", team, privateKey);
		entityManager.persist(team);
		entityManager.persist(user);
		entityManager.getTransaction().commit();
		verify(algorithm);
	}

	@After
	public void tearDown() {
		persistService.stop();
	}

	@Test
	public void testDoPostNoVersion() throws IOException, ServletException {
		initRequestMock(ImmutableMap.of("name", "testuser", "password",
				"password"));
		res.sendError(HttpServletResponse.SC_BAD_REQUEST);
		expectLastCall();

		replay(req, res);
		servlet.doPost(req, res);
		verify(req, res);
	}

	@Test
	public void testDoPostNoName() throws IOException, ServletException {
		initRequestMock(ImmutableMap.of("version", "0.2-alpha", "password",
				"password"));
		res.sendError(HttpServletResponse.SC_BAD_REQUEST);
		expectLastCall();

		replay(req, res);
		servlet.doPost(req, res);
		verify(req, res);
	}

	@Test
	public void testDoPostNoPassword() throws IOException, ServletException {
		initRequestMock(ImmutableMap.of("version", "0.2-alpha", "name",
				"testuser"));
		res.sendError(HttpServletResponse.SC_BAD_REQUEST);
		expectLastCall();

		replay(req, res);
		servlet.doPost(req, res);
		verify(req, res);
	}

	@Test
	public void testDoPostVersion() throws IOException, ServletException {
		initRequestMock(ImmutableMap.of("version", "0.1-alpha", "name",
				"testuser", "password", "password"));
		res.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED);
		expectLastCall();

		replay(req, res);
		servlet.doPost(req, res);
		verify(req, res);
	}

	@Test
	public void testDoPostFakeUser() throws IOException, ServletException {
		initRequestMock(ImmutableMap.of("version", "0.2-alpha", "name",
				"fakeuser", "password", "password"));
		res.sendError(HttpServletResponse.SC_FORBIDDEN);
		expectLastCall();

		replay(req, res);
		servlet.doPost(req, res);
		verify(req, res);
	}

	@Test
	public void testDoPostBadPassword() throws IOException, ServletException {
		initRequestMock(ImmutableMap.of("version", "0.2-alpha", "name",
				"testuser", "password", "foobar"));
		res.sendError(HttpServletResponse.SC_FORBIDDEN);
		expectLastCall();

		replay(req, res);
		servlet.doPost(req, res);
		verify(req, res);
	}

	@Test
	public void testDoPost() throws IOException, ServletException {
		initRequestMock(ImmutableMap.of("version", "0.2-alpha", "name",
				"testuser", "password", "password"));
		expect(req.getSession(true)).andReturn(session).anyTimes();
		res.setStatus(HttpServletResponse.SC_OK);
		expectLastCall().anyTimes();

		replay(req, res, session);
		servlet.doPost(req, res);
		verify(req, res, session);
	}

	@Test
	public void testDoDelete() throws IOException, ServletException {
		expect(req.getSession(false)).andReturn(session).anyTimes();
		session.invalidate();
		expectLastCall();
		res.setStatus(HttpServletResponse.SC_OK);
		expectLastCall().anyTimes();

		replay(req, res, session);
		servlet.doDelete(req, res);
		verify(req, res, session);
	}

	private void initRequestMock(Map<String, String> params) {
		for (Map.Entry<String, String> entry : params.entrySet()) {
			expect(req.getParameter(entry.getKey()))
					.andReturn(entry.getValue()).anyTimes();
			expect(req.getParameterValues(entry.getKey())).andReturn(
					new String[] { entry.getValue() }).anyTimes();
		}

		expect(req.getParameterMap()).andReturn(
				Maps.transformValues(params, new Function<String, String[]>() {
					@Override
					public String[] apply(String input) {
						return new String[] { input };
					}
				})).anyTimes();

		expect(req.getParameterNames()).andReturn(
				Iterators.asEnumeration(params.keySet().iterator())).anyTimes();
	}
}
