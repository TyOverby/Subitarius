/*
 * ParseModule.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.instance.server.parse;

import com.google.inject.AbstractModule;

public final class ParseModule extends AbstractModule {
	public ParseModule() {}
	
	@Override
	protected void configure() {
		bind(ArticleParser.class).to(MasterArticleParser.class);
	}
}
