/*
 * ArticleParserFactory.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.server.parse;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.prealpha.extempdb.instance.domain.Source;

/*
 * TODO: it would be nice if this were done without this factory
 */
public final class ArticleParserFactory {
	private final Injector injector;

	@Inject
	private ArticleParserFactory(Injector injector) {
		this.injector = injector;
	}

	public ArticleParser getParser(Source source) {
		Class<? extends ArticleParser> klass;
		switch (source) {
		case NY_TIMES:
			klass = NyTimesArticleParser.class;
			break;
		case WASHINGTON_POST:
			klass = WaPostArticleParser.class;
			break;
		case CS_MONITOR:
			klass = CsmArticleParser.class;
			break;
		case WS_JOURNAL:
			klass = WsjArticleParser.class;
			break;
		case REUTERS:
			klass = ReutersArticleParser.class;
			break;
		case GUARDIAN:
			klass = GuardianArticleParser.class;
			break;
		case ECONOMIST:
			klass = EconomistArticleParser.class;
			break;
		default:
			throw new ParserNotFoundException(source);
		}
		return injector.getInstance(klass);
	}
}
