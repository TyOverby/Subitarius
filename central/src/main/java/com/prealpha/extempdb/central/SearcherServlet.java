/*
 * SearcherServlet.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.central;

import java.io.IOException;
import java.net.InetAddress;
import java.util.EnumSet;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.UnitOfWork;
import com.prealpha.extempdb.central.search.Searcher;
import com.prealpha.extempdb.domain.Source;

/**
 * Accepts requests to begin searching for article URLs using {@link Searcher}.
 * HTTP {@code POST} is the only method implemented by this servlet; see
 * {@link #doPost(HttpServletRequest, HttpServletResponse) doPost} for details.
 * 
 * @author Meyer Kizner
 * @see #doPost(HttpServletRequest, HttpServletResponse)
 * 
 */
final class SearcherServlet extends HttpServlet {
	private final UnitOfWork unitOfWork;

	private final Provider<Searcher> searcherProvider;

	@Inject
	private SearcherServlet(UnitOfWork unitOfWork,
			Provider<Searcher> searcherProvider) {
		this.unitOfWork = unitOfWork;
		this.searcherProvider = searcherProvider;
	}

	/**
	 * Services a request to begin a new search. The request must originate from
	 * the local machine; this is checked by comparing the local and remote IP
	 * addresses. If they are equal, a separate thread is started for the search
	 * and status code 200 (OK) is immediately sent in response. Otherwise, no
	 * search is performed and status code 403 (Forbidden) is sent instead.
	 * <p>
	 * 
	 * One parameter is recognized by this method, {@code sourceOrdinals}. If
	 * present, its value is used to limit the sources which will be searched.
	 * The value is interpreted as a comma-separated set of integers; each
	 * integer corresponds to a {@link Source} ordinal. All sources will be
	 * skipped if the value is empty. The set's order is disregarded and does
	 * not influence the search order; duplicates are similarly ignored. If
	 * {@code sourceOrdinals} is absent, all sources are searched.
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res)
			throws IOException, ServletException {
		InetAddress localAddress = InetAddress.getByName(req.getLocalAddr());
		InetAddress remoteAddress = InetAddress.getByName(req.getRemoteAddr());

		if (localAddress.equals(remoteAddress)) {
			final Set<Source> sources = parseSourceOrdinals(req);

			new Thread(new Runnable() {
				@Override
				public void run() {
					unitOfWork.begin();
					try {
						Searcher searcher = searcherProvider.get();
						searcher.run(sources);
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

	private static Set<Source> parseSourceOrdinals(HttpServletRequest req) {
		String sourceOrdinals = req.getParameter("sourceOrdinals");
		if (sourceOrdinals == null) {
			return EnumSet.allOf(Source.class);
		} else {
			Set<Source> sources = EnumSet.noneOf(Source.class);
			String[] tokens = sourceOrdinals.split(",");
			for (String token : tokens) {
				int ordinal = Integer.parseInt(token);
				sources.add(Source.values()[ordinal]);
			}
			return sources;
		}
	}
}
