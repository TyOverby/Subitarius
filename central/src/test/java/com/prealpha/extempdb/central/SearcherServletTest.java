/*
 * SearcherServletTest.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.central;

import static org.easymock.EasyMock.*;

import java.io.IOException;
import java.util.EnumSet;
import java.util.concurrent.Executor;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Inject;
import com.google.inject.persist.UnitOfWork;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.easymock.Mock;
import com.mycila.testing.plugin.guice.Bind;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.prealpha.extempdb.central.search.Searcher;
import com.prealpha.extempdb.domain.Source;
import com.prealpha.extempdb.util.logging.TestLoggingModule;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ TestLoggingModule.class })
public final class SearcherServletTest {
	@Inject
	private SearcherServlet servlet;

	@Mock(Mock.Type.NICE)
	@Bind
	private HttpServletRequest req;

	@Mock(Mock.Type.STANDARD)
	@Bind
	private HttpServletResponse res;

	@SuppressWarnings("unused")
	@Bind
	private final Executor executor = new Executor() {
		@Override
		public void execute(Runnable runnable) {
			runnable.run();
		}
	};

	@Mock(Mock.Type.NICE)
	@Bind
	private UnitOfWork unitOfWork;

	@Mock(Mock.Type.STANDARD)
	@Bind
	private Searcher searcher;

	@Test
	public void testDoPost() throws IOException, ServletException {
		expect(req.getLocalAddr()).andReturn("127.0.0.1").anyTimes();
		expect(req.getRemoteAddr()).andReturn("127.0.0.1").anyTimes();
		searcher.run(eq(EnumSet.allOf(Source.class)));
		expectLastCall();
		res.setStatus(HttpServletResponse.SC_OK);
		expectLastCall().anyTimes();

		replay(req, res, unitOfWork, searcher);
		servlet.doPost(req, res);
		verify(req, res, unitOfWork, searcher);
	}

	@Test
	public void testDoPostLimited() throws IOException, ServletException {
		expect(req.getLocalAddr()).andReturn("127.0.0.1").anyTimes();
		expect(req.getRemoteAddr()).andReturn("127.0.0.1").anyTimes();
		expect(req.getParameter("sourceOrdinals")).andReturn("0,5").anyTimes();
		searcher.run(eq(EnumSet.of(Source.NY_TIMES, Source.GUARDIAN)));
		expectLastCall();
		res.setStatus(HttpServletResponse.SC_OK);
		expectLastCall().anyTimes();

		replay(req, res, unitOfWork, searcher);
		servlet.doPost(req, res);
		verify(req, res, unitOfWork, searcher);
	}

	@Test
	public void testDoPostNonLocal() throws IOException, ServletException {
		expect(req.getLocalAddr()).andReturn("127.0.0.1").anyTimes();
		expect(req.getRemoteAddr()).andReturn("255.255.255.255").anyTimes();
		res.sendError(HttpServletResponse.SC_FORBIDDEN);
		expectLastCall();

		replay(req, res, unitOfWork, searcher);
		servlet.doPost(req, res);
		verify(req, res, unitOfWork, searcher);
	}

	@Test
	public void testDoPostMalformed() throws IOException, ServletException {
		expect(req.getLocalAddr()).andReturn("127.0.0.1").anyTimes();
		expect(req.getRemoteAddr()).andReturn("127.0.0.1").anyTimes();
		expect(req.getParameter("sourceOrdinals")).andReturn("foo").anyTimes();
		res.sendError(HttpServletResponse.SC_BAD_REQUEST);
		expectLastCall();

		replay(req, res, unitOfWork, searcher);
		servlet.doPost(req, res);
		verify(req, res, unitOfWork, searcher);
	}

	@Test
	public void testDoPostBounds() throws IOException, ServletException {
		expect(req.getLocalAddr()).andReturn("127.0.0.1").anyTimes();
		expect(req.getRemoteAddr()).andReturn("127.0.0.1").anyTimes();
		expect(req.getParameter("sourceOrdinals")).andReturn("-1").anyTimes();
		res.sendError(HttpServletResponse.SC_BAD_REQUEST);
		expectLastCall();

		replay(req, res, unitOfWork, searcher);
		servlet.doPost(req, res);
		verify(req, res, unitOfWork, searcher);
	}
}
