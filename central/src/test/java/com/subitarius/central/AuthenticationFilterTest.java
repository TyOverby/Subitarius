/*
 * AuthenticationFilterTest.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.central;

import static com.google.common.base.Preconditions.*;
import static org.easymock.EasyMock.*;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.easymock.Mock;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;
import com.subitarius.domain.Team;
import com.subitarius.util.logging.TestLoggingModule;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ TestLoggingModule.class })
public final class AuthenticationFilterTest {
	@SuppressWarnings("unused")
	@ModuleProvider
	private Module getModule() {
		return new AbstractModule() {
			@Override
			protected void configure() {
				bind(Team.class).toProvider(teamProvider);
			}
		};
	}

	@Inject
	private AuthenticationFilter filter;

	private final TeamProvider teamProvider = new TeamProvider();

	@Mock(Mock.Type.NICE)
	private Team team;

	@Mock(Mock.Type.NICE)
	private HttpServletRequest req;

	@Mock(Mock.Type.STANDARD)
	private HttpServletResponse res;

	@Mock(Mock.Type.STANDARD)
	private FilterChain chain;

	@Test
	public void testDoFilterNotAuthenticated() throws IOException,
			ServletException {
		teamProvider.init(true);
		res.sendError(HttpServletResponse.SC_UNAUTHORIZED);
		expectLastCall();

		replay(req, res, chain);
		filter.doFilter(req, res, chain);
		verify(req, res, chain);
	}

	@Test
	public void testDoFilterExpired() throws IOException, ServletException {
		teamProvider.init(false);
		expect(team.isExpired()).andReturn(true).anyTimes();
		res.sendError(HttpServletResponse.SC_UNAUTHORIZED);
		expectLastCall();

		replay(team, req, res, chain);
		filter.doFilter(req, res, chain);
		verify(team, req, res, chain);
	}

	@Test
	public void testDoFilter() throws IOException, ServletException {
		teamProvider.init(false);
		expect(team.isExpired()).andReturn(false).anyTimes();
		chain.doFilter(req, res);
		expectLastCall();
		res.setStatus(HttpServletResponse.SC_OK);
		expectLastCall().anyTimes();

		replay(team, req, res, chain);
		filter.doFilter(req, res, chain);
		verify(team, req, res, chain);
	}

	private class TeamProvider implements Provider<Team> {
		private boolean initialized;

		private boolean isNull;

		public void init(boolean isNull) {
			checkState(!initialized);
			initialized = true;
			this.isNull = isNull;
		}

		@Override
		public Team get() {
			checkState(initialized);
			return (isNull ? null : team);
		}
	}
}
