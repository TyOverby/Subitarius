/*
 * ArticleParser.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.server.parse;

import com.prealpha.extempdb.domain.Article;
import com.prealpha.extempdb.domain.ArticleUrl;

public interface ArticleParser {
	/**
	 * Parses the article located at the given canonical URL into a full
	 * {@link Article}. Generally, this involves turning the HTML markup into a
	 * parseable XML DOM, and retrieving specific elements from the DOM in order
	 * to fill the {@code Article} fields. Parsers do, occasionally, have bugs
	 * or run into problems, so an {@link ArticleParseException} can be thrown
	 * and should be handled appropriately by callers.
	 * <p>
	 * 
	 * If {@code null} is returned from this method, it indicates that the
	 * canonical URL cannot be parsed into a valid article. This may arise if
	 * there are non-standard issues, such as corrections which are unreflected
	 * in body text, a missing title, or a page such as an interactive feature
	 * that is not represented in text.
	 * 
	 * @param articleUrl
	 *            the canonical URL to parse
	 * @return a full {@link Article} obtained by parsing the article at the
	 *         given canonical URL, or {@code null} if the article is
	 *         unparseable into a standard form
	 * @throws NullPointerException
	 *             if {@code url} is {@code null}
	 * @throws ArticleParseException
	 *             if the parser is unable to complete for an unexpected reason,
	 *             such as a change in the format in which articles are served
	 *             by the source
	 */
	Article parse(ArticleUrl articleUrl) throws ArticleParseException;
}
