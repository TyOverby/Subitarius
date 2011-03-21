/*
 * SearcherServlet.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.prealpha.extempdb.server.search.Searcher;
import com.wideplay.warp.persist.WorkManager;

final class SearcherServlet extends HttpServlet {
	private final WorkManager workManager;

	private final Provider<Searcher> searcherProvider;

	@Inject
	public SearcherServlet(WorkManager workManager,
			Provider<Searcher> searcherProvider) {
		this.workManager = workManager;
		this.searcherProvider = searcherProvider;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res)
			throws IOException, ServletException {
		// parse the requested sources
		String rawSources = req.getParameter("sources");
		Set<Integer> sources;
		if (rawSources == null) {
			sources = Collections.<Integer> emptySet();
		} else {
			sources = new HashSet<Integer>();
			String[] tokens = rawSources.split(",");
			for (String token : tokens) {
				sources.add(Integer.parseInt(token));
			}
		}

		InetAddress localAddress = InetAddress.getByName(req.getLocalAddr());
		InetAddress remoteAddress = InetAddress.getByName(req.getRemoteAddr());

		if (localAddress.equals(remoteAddress)) {
			final Set<Long> sourceIds = parseSourceIds(req);

			new Thread(new Runnable() {
				@Override
				public void run() {
					workManager.beginWork();
					try {
						Searcher searcher = searcherProvider.get();
						searcher.run(sourceIds);
					} finally {
						workManager.endWork();
					}
				}
			}, "Searcher").start();

			res.setStatus(HttpServletResponse.SC_OK);
		} else {
			res.setStatus(HttpServletResponse.SC_FORBIDDEN);
		}
	}

	private Set<Long> parseSourceIds(HttpServletRequest req) {
		String rawSourceIds = req.getParameter("sourceIds");
		if (rawSourceIds == null) {
			return Collections.<Long> emptySet();
		} else {
			Set<Long> sourceIds = new HashSet<Long>();
			String[] tokens = rawSourceIds.split(",");
			for (String token : tokens) {
				sourceIds.add(Long.parseLong(token));
			}
			return sourceIds;
		}
	}
}
