/*
 * MasterArticleParser.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.instance.server.parse;

import javax.inject.Inject;

import com.google.inject.Injector;
import com.subitarius.domain.Article;
import com.subitarius.domain.ArticleUrl;
import com.subitarius.domain.Source;

final class MasterArticleParser implements ArticleParser {
	private final Injector injector;

	@Inject
	private MasterArticleParser(Injector injector) {
		this.injector = injector;
	}

	@Override
	public Article parse(ArticleUrl articleUrl) throws ArticleParseException {
		String url = articleUrl.getUrl();
		Source source = Source.fromUrl(url);
		if (source == null) {
			throw new ParserNotFoundException(url);
		}

		Class<? extends ArticleParser> parserClass;
		switch (source) {
		case NY_TIMES:
			parserClass = NyTimesArticleParser.class;
			break;
		case WASHINGTON_POST:
			parserClass = WaPostArticleParser.class;
			break;
		case CS_MONITOR:
			parserClass = CsmArticleParser.class;
			break;
		case WS_JOURNAL:
			parserClass = WsjArticleParser.class;
			break;
		case REUTERS:
			parserClass = ReutersArticleParser.class;
			break;
		case GUARDIAN:
			parserClass = GuardianArticleParser.class;
			break;
		case ECONOMIST:
			parserClass = EconomistArticleParser.class;
			break;
		case BBC:
			parserClass = BbcArticleParser.class;
			break;
		case AL_JAZEERA:
			parserClass = AlJazeeraArticleParser.class;
			break;
		case LA_TIMES:
			parserClass = LaTimesArticleParser.class;
			break;
		default:
			throw new ParserNotFoundException(source);
		}

		ArticleParser parser = injector.getInstance(parserClass);
		return parser.parse(articleUrl);
	}
}
