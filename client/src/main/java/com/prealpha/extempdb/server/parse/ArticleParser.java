/*
 * ArticleParser.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server.parse;

public interface ArticleParser {
	/**
	 * Returns the canonical form of the given URL. The canonical form uniquely
	 * identifies a particular article such that no other canonical URL will
	 * result in the same {@link ProtoArticle} result from the
	 * {@link #parse(String)} method.
	 * 
	 * @param url
	 *            the non-{@code null} raw URL to canonicalize
	 * @return the canonical form of the given URL
	 */
	String getCanonicalUrl(String url);

	/**
	 * Parses the article located at the given canonical URL into a
	 * {@link ProtoArticle}. Generally, this involves turning the HTML markup
	 * into a parseable XML DOM, and retrieving specific elements from the DOM
	 * in order to fill the {@link ProtoArticle} fields. Parsers do,
	 * occasionally, have bugs or run into problems, so an
	 * {@link ArticleParseException} can be thrown and should be handled
	 * appropriately by callers.
	 * <p>
	 * 
	 * Note that the URL <i>must</i> be a canonical URL, as returned by
	 * {@link #getCanonicalUrl(String)}. If the URL is non-canonical, the
	 * results of the parse are undefined.
	 * <p>
	 * 
	 * If {@code null} is returned from this method, it indicates that the
	 * canonical URL cannot be parsed into a valid article. This may arise if
	 * there are non-standard issues, such as corrections which are unreflected
	 * in body text, a missing title, or a page such as an interactive feature
	 * that is not represented in text.
	 * 
	 * @param url
	 *            the non-{@code null} canonical URL to parse
	 * @return a {@link ProtoArticle} obtained by parsing the article at the
	 *         given canonical URL, or {@code null} if the article is
	 *         unparseable into a standard form
	 * @throws ArticleParseException
	 *             if the parser is unable to complete for an unexpected reason,
	 *             such as a change in the format in which articles are served
	 *             by the source
	 */
	ProtoArticle parse(String url) throws ArticleParseException;
}
