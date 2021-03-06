/*
 * SearchModule.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.central.search;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;

public final class SearchModule extends AbstractModule {
	public SearchModule() {
	}

	@Override
	protected void configure() {
		bind(SearchProvider.class).to(BingSearchProvider.class);
	}

	@Provides
	@Inject
	Gson getGson(GsonBuilder builder) {
		builder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		builder.setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE);
		return builder.create();
	}
}
