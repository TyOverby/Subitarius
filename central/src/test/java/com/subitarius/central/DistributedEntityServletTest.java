/*
 * DistributedEntityServletTest.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.central;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.easymock.Mock;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;
import com.subitarius.domain.ArticleUrl;
import com.subitarius.domain.DistributedEntity;
import com.subitarius.domain.DomainModule;
import com.subitarius.util.logging.TestLoggingModule;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ TestLoggingModule.class })
public final class DistributedEntityServletTest {
	@SuppressWarnings("unused")
	@ModuleProvider
	private static Iterable<Module> getModules() {
		return ImmutableList.<Module> of(new JpaPersistModule("central-test"),
				new DomainModule());
	}

	@Inject
	private DistributedEntityServlet servlet;

	@Inject
	private PersistService persistService;

	@Inject
	private Provider<EntityManager> entityManagerProvider;

	@Mock(Mock.Type.NICE)
	private HttpServletRequest req;

	@Mock(Mock.Type.STANDARD)
	private HttpServletResponse res;

	private List<ArticleUrl> urls;

	@Before
	public void setUp() {
		persistService.start();
		EntityManager entityManager = entityManagerProvider.get();
		entityManager.getTransaction().begin();
		urls = Lists.newArrayListWithCapacity(50);
		for (int i = 0; i < 50; i++) {
			ArticleUrl url = new ArticleUrl("http://www.nytimes.com/article"
					+ i);
			entityManager.persist(url);
			urls.add(url);
		}
		entityManager.getTransaction().commit();
	}

	@After
	public void tearDown() {
		persistService.stop();
	}

	@Test
	public void testDoGetAll() throws IOException, ServletException,
			ClassNotFoundException {
		expect(req.getParameter("version")).andReturn("0.2-alpha");
		expect(req.getParameter("timestamp")).andReturn(null);
		expect(req.getParameter("prefix")).andReturn(null);
		testDoGetEntities(urls);
	}

	@Test
	public void testDoGetTimestamp() throws IOException, ServletException,
			ClassNotFoundException {
		expect(req.getParameter("version")).andReturn("0.2-alpha");
		expect(req.getParameter("timestamp")).andReturn(
				Long.toString(Long.MAX_VALUE));
		expect(req.getParameter("prefix")).andReturn(null);
		testDoGetEntities(ImmutableList.<DistributedEntity> of());
	}

	@Test
	public void testDoGetPrefix() throws IOException, ServletException,
			ClassNotFoundException {
		final String prefix = urls.get(0).getHash().substring(0, 1);
		expect(req.getParameter("version")).andReturn("0.2-alpha");
		expect(req.getParameter("timestamp")).andReturn(null);
		expect(req.getParameter("prefix")).andReturn(prefix);
		testDoGetEntities(Collections2.filter(urls,
				new Predicate<ArticleUrl>() {
					@Override
					public boolean apply(ArticleUrl url) {
						return (url.getHash().startsWith(prefix));
					}
				}));
	}

	@Test
	public void testDoGetBoth() throws IOException, ServletException,
			ClassNotFoundException {
		final long timestamp = urls.get(25).getPersistDate().getTime();
		final String prefix = urls.get(25).getHash().substring(0, 1);
		expect(req.getParameter("version")).andReturn("0.2-alpha");
		expect(req.getParameter("timestamp")).andReturn(
				Long.toString(timestamp));
		expect(req.getParameter("prefix")).andReturn(prefix);
		testDoGetEntities(Collections2.filter(urls,
				new Predicate<ArticleUrl>() {
					@Override
					public boolean apply(ArticleUrl url) {
						boolean timestampApplies = (url.getPersistDate()
								.getTime() >= timestamp);
						boolean prefixApplies = (url.getHash()
								.startsWith(prefix));
						return (timestampApplies && prefixApplies);
					}
				}));
	}

	private void testDoGetEntities(
			Collection<? extends DistributedEntity> entities)
			throws IOException, ServletException, ClassNotFoundException {
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		expect(res.getOutputStream()).andReturn(new ServletOutputStream() {
			@Override
			public void write(int b) throws IOException {
				baos.write(b);
			}
		}).anyTimes();
		res.setStatus(HttpServletResponse.SC_OK);
		expectLastCall().anyTimes();

		replay(req, res);
		servlet.doGet(req, res);
		verify(req, res);

		byte[] bytes = baos.toByteArray();
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		ObjectInputStream ois = new ObjectInputStream(bais);
		assertEquals(entities.size(), ois.readInt());
		Set<DistributedEntity> deserialized = Sets
				.newHashSetWithExpectedSize(entities.size());
		for (int i = 0; i < entities.size(); i++) {
			deserialized.add((DistributedEntity) ois.readObject());
		}
		assertEquals(entities.size(), deserialized.size());
		for (DistributedEntity entity : entities) {
			assertTrue(deserialized.contains(entity));
		}
	}

	@Test
	public void testDoGetBadVersion() throws IOException, ServletException {
		expect(req.getParameter("version")).andReturn("0.1-alpha");
		expect(req.getParameter("timestamp")).andReturn(null).anyTimes();
		expect(req.getParameter("prefix")).andReturn(null).anyTimes();
		res.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED);
		expectLastCall();

		replay(req, res);
		servlet.doGet(req, res);
		verify(req, res);
	}

	@Test
	public void testDoGetBadTimestamp() throws IOException, ServletException {
		expect(req.getParameter("version")).andReturn("0.2-alpha");
		expect(req.getParameter("timestamp")).andReturn("foo bar");
		expect(req.getParameter("prefix")).andReturn(null).anyTimes();
		res.sendError(HttpServletResponse.SC_BAD_REQUEST);
		expectLastCall();

		replay(req, res);
		servlet.doGet(req, res);
		verify(req, res);
	}
}
