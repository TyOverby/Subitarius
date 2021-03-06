/*
 * SearcherServlet.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.central;

import java.io.IOException;
import java.net.InetAddress;
import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.Executor;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.UnitOfWork;
import com.subitarius.central.search.Searcher;
import com.subitarius.domain.Source;
import com.subitarius.util.logging.InjectLogger;

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
	private static final long serialVersionUID = 1L;

	@InjectLogger
	private Logger log;

	private final Executor executor;

	private final UnitOfWork unitOfWork;

	private final Provider<Searcher> searcherProvider;

	@Inject
	private SearcherServlet(Executor executor, UnitOfWork unitOfWork,
			Provider<Searcher> searcherProvider) {
		this.executor = executor;
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
	 * integer corresponds to a {@link Source} ordinal. If the value cannot be
	 * parsed as such, status code 400 (bad request) is sent to the client. The
	 * set's order is disregarded and does not influence the search order;
	 * duplicates are similarly ignored. If {@code sourceOrdinals} is absent,
	 * all sources are searched.
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res)
			throws IOException, ServletException {
		InetAddress localAddress = InetAddress.getByName(req.getLocalAddr());
		InetAddress remoteAddress = InetAddress.getByName(req.getRemoteAddr());
		if (localAddress.equals(remoteAddress)) {
			try {
				final Set<Source> sources = parseSourceOrdinals(req);
				log.info("accepted request to search sources: {}", sources);
				executor.execute(new Runnable() {
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
				});
				res.setStatus(HttpServletResponse.SC_OK);
			} catch (IllegalArgumentException iax) {
				log.warn("bad sourceOrdinals parameter: {}",
						req.getParameter("sourceOrdinals"));
				res.sendError(HttpServletResponse.SC_BAD_REQUEST);
			}
		} else {
			log.info("rejected request to search from IP: {}", remoteAddress);
			res.sendError(HttpServletResponse.SC_FORBIDDEN);
		}
	}

	/**
	 * Parses the {@code sourceOrdinals} parameter on the request into a set of
	 * sources to search. The parameter is interpreted as a comma-separated list
	 * of {@link Source} ordinals.
	 * 
	 * @param req
	 *            the request to parse
	 * @return a set of sources to search based on the request
	 * @throws IllegalArgumentException
	 *             if the parameter contains ordinals which are non-numeric or
	 *             out of bounds
	 * @see #doPost(HttpServletRequest, HttpServletResponse)
	 */
	private static Set<Source> parseSourceOrdinals(HttpServletRequest req) {
		String sourceOrdinals = req.getParameter("sourceOrdinals");
		if (sourceOrdinals == null) {
			return EnumSet.allOf(Source.class);
		} else {
			Set<Source> sources = EnumSet.noneOf(Source.class);
			String[] tokens = sourceOrdinals.split(",");
			for (String token : tokens) {
				try {
					int ordinal = Integer.parseInt(token);
					if ((ordinal < 0) || (ordinal >= Source.values().length)) {
						throw new IllegalArgumentException(
								"ordinal out of bounds: " + ordinal);
					}
					sources.add(Source.values()[ordinal]);
				} catch (NumberFormatException nfx) {
					throw new IllegalArgumentException(nfx);
				}
			}
			return sources;
		}
	}
}
