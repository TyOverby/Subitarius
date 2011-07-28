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
import com.google.inject.persist.UnitOfWork;
import com.prealpha.extempdb.server.search.Searcher;

final class SearcherServlet extends HttpServlet {
	private final UnitOfWork unitOfWork;

	private final Provider<Searcher> searcherProvider;

	@Inject
	public SearcherServlet(UnitOfWork unitOfWork,
			Provider<Searcher> searcherProvider) {
		this.unitOfWork = unitOfWork;
		this.searcherProvider = searcherProvider;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res)
			throws IOException, ServletException {
		InetAddress localAddress = InetAddress.getByName(req.getLocalAddr());
		InetAddress remoteAddress = InetAddress.getByName(req.getRemoteAddr());

		if (localAddress.equals(remoteAddress)) {
			final Set<Long> sourceOrdinals = parseSourceOrdinals(req);

			new Thread(new Runnable() {
				@Override
				public void run() {
					unitOfWork.begin();
					try {
						Searcher searcher = searcherProvider.get();
						searcher.run(sourceOrdinals);
					} finally {
						unitOfWork.end();
					}
				}
			}, "Searcher").start();

			res.setStatus(HttpServletResponse.SC_OK);
		} else {
			res.setStatus(HttpServletResponse.SC_FORBIDDEN);
		}
	}

	private Set<Long> parseSourceOrdinals(HttpServletRequest req) {
		String rawSourceIds = req.getParameter("sourceOrdinals");
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
