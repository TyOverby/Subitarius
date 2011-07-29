/*
 * SearcherServlet.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.central.search;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.UnitOfWork;
import com.prealpha.extempdb.central.search.Searcher;

public final class SearcherServlet extends HttpServlet {
	private final UnitOfWork unitOfWork;

	private final Provider<Searcher> searcherProvider;

	@Inject
	private SearcherServlet(UnitOfWork unitOfWork,
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
			final Set<Integer> sourceOrdinals = parseSourceOrdinals(req);

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
			res.sendError(HttpServletResponse.SC_FORBIDDEN);
		}
	}

	private Set<Integer> parseSourceOrdinals(HttpServletRequest req) {
		String rawSourceIds = req.getParameter("sourceOrdinals");
		if (rawSourceIds == null) {
			return null;
		} else {
			Set<Integer> sourceIds = Sets.newHashSet();
			String[] tokens = rawSourceIds.split(",");
			for (String token : tokens) {
				sourceIds.add(Integer.parseInt(token));
			}
			return sourceIds;
		}
	}
}
