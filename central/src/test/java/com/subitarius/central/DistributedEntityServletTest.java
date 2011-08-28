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
import java.io.PrintWriter;

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

import com.google.common.collect.ImmutableList;
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

	@Mock(Mock.Type.NICE)
	private PrintWriter writer;

	private ArticleUrl url1;

	private ArticleUrl url2;

	@Before
	public void setUp() {
		persistService.start();
		EntityManager entityManager = entityManagerProvider.get();
		entityManager.getTransaction().begin();
		url1 = new ArticleUrl("http://www.nytimes.com/");
		entityManager.persist(url1);
		url2 = new ArticleUrl("http://www.washingtonpost.com/");
		entityManager.persist(url2);
		entityManager.getTransaction().commit();
	}

	@After
	public void tearDown() {
		persistService.stop();
	}

	@Test
	public void testDoGetEmptyPath() throws IOException, ServletException {
		expect(req.getPathInfo()).andReturn("").anyTimes();
		expect(res.getWriter()).andReturn(writer).anyTimes();
		writer.println(url1.getHash());
		expectLastCall();
		writer.println(url2.getHash());
		expectLastCall();

		replay(req, res, writer);
		servlet.doGet(req, res);
		verify(req, res, writer);
	}

	@Test
	public void testDoGetSlashPath() throws IOException, ServletException {
		expect(req.getPathInfo()).andReturn("").anyTimes();
		expect(res.getWriter()).andReturn(writer).anyTimes();
		writer.println(url1.getHash());
		expectLastCall();
		writer.println(url2.getHash());
		expectLastCall();
		res.setStatus(HttpServletResponse.SC_OK);
		expectLastCall().anyTimes();

		replay(req, res, writer);
		servlet.doGet(req, res);
		verify(req, res, writer);
	}

	@Test
	public void testDoGetUrl1() throws IOException, ServletException,
			ClassNotFoundException {
		testDoGetEntity(url1);
	}

	@Test
	public void testDoGetUrl2() throws IOException, ServletException,
			ClassNotFoundException {
		testDoGetEntity(url2);
	}

	private void testDoGetEntity(DistributedEntity entity) throws IOException,
			ServletException, ClassNotFoundException {
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		expect(req.getPathInfo()).andReturn('/' + entity.getHash()).anyTimes();
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
		DistributedEntity deserialized = (DistributedEntity) ois.readObject();
		assertEquals(entity, deserialized);
	}

	@Test
	public void testDoGetNotFound() throws IOException, ServletException {
		expect(req.getPathInfo()).andReturn("/0").anyTimes();
		res.sendError(HttpServletResponse.SC_NOT_FOUND);
		expectLastCall();

		replay(req, res);
		servlet.doGet(req, res);
		verify(req, res);
	}
}
