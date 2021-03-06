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
import com.google.common.collect.ImmutableSet;
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
import com.subitarius.domain.Tag;
import com.subitarius.domain.Tag.Type;
import com.subitarius.domain.TagMapping;
import com.subitarius.util.logging.TestLoggingModule;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ TestLoggingModule.class })
public final class DistributedEntityServletTest {
	private static final int URL_COUNT = 50;

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

	private List<DistributedEntity> entities;

	@Before
	public void setUp() {
		persistService.start();
		EntityManager entityManager = entityManagerProvider.get();
		entityManager.getTransaction().begin();

		Tag tag = new Tag("Fake URLs", Type.SEARCHED, ImmutableSet.<Tag> of());
		entityManager.persist(tag);

		entities = Lists.newArrayListWithCapacity(2 * URL_COUNT);
		for (int i = 0; i < URL_COUNT; i++) {
			ArticleUrl url = new ArticleUrl("http://www.nytimes.com/article"
					+ i);
			entityManager.persist(url);
			entities.add(url);

			TagMapping mapping = new TagMapping(tag, url);
			entityManager.persist(mapping);
			entities.add(mapping);
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
		testDoGetEntities(entities);
	}

	@Test
	public void testDoGetNone() throws IOException, ServletException,
			ClassNotFoundException {
		expect(req.getParameter("version")).andReturn("0.2-alpha");
		expect(req.getParameter("timestamp")).andReturn(
				Long.toString(Long.MAX_VALUE));
		testDoGetEntities(ImmutableList.<DistributedEntity> of());
	}

	@Test
	public void testDoGetSome() throws IOException, ServletException,
			ClassNotFoundException {
		final long timestamp = entities.get(URL_COUNT).getPersistDate()
				.getTime();
		expect(req.getParameter("version")).andReturn("0.2-alpha");
		expect(req.getParameter("timestamp")).andReturn(
				Long.toString(timestamp));
		testDoGetEntities(Collections2.filter(entities,
				new Predicate<DistributedEntity>() {
					@Override
					public boolean apply(DistributedEntity input) {
						return (input.getPersistDate().getTime() >= timestamp);
					}
				}));
	}

	private void testDoGetEntities(
			Collection<? extends DistributedEntity> entities)
			throws IOException, ServletException, ClassNotFoundException {
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		res.setContentType("application/x-java-serialized-object");
		expectLastCall();
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
		res.sendError(HttpServletResponse.SC_BAD_REQUEST);
		expectLastCall();

		replay(req, res);
		servlet.doGet(req, res);
		verify(req, res);
	}
}
