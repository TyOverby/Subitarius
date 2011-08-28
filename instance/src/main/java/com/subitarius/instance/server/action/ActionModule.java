/*
 * ActionModule.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.instance.server.action;

import java.util.List;

import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.prealpha.dispatch.server.ActionHandlerModule;
import com.subitarius.instance.shared.action.AddArticle;
import com.subitarius.instance.shared.action.AddMapping;
import com.subitarius.instance.shared.action.GetArticleByHash;
import com.subitarius.instance.shared.action.GetArticleByUrl;
import com.subitarius.instance.shared.action.GetHierarchy;
import com.subitarius.instance.shared.action.GetMapping;
import com.subitarius.instance.shared.action.GetMappingsByArticle;
import com.subitarius.instance.shared.action.GetMappingsByTag;
import com.subitarius.instance.shared.action.GetParagraphs;
import com.subitarius.instance.shared.action.GetTag;
import com.subitarius.instance.shared.action.GetTagSuggestions;

public class ActionModule extends ActionHandlerModule {
	public ActionModule() {
	}

	@Override
	protected void configure() {
		bindHandler(AddArticle.class, AddArticleHandler.class);
		bindHandler(AddMapping.class, AddMappingHandler.class);
		bindHandler(GetArticleByHash.class, GetArticleByHashHandler.class);
		bindHandler(GetArticleByUrl.class, GetArticleByUrlHandler.class);
		bindHandler(GetHierarchy.class, GetHierarchyHandler.class);
		bindHandler(GetMapping.class, GetMappingHandler.class);
		bindHandler(GetMappingsByArticle.class,
				GetMappingsByArticleHandler.class);
		bindHandler(GetMappingsByTag.class, GetMappingsByTagHandler.class);
		bindHandler(GetParagraphs.class, GetParagraphsHandler.class);
		bindHandler(GetTag.class, GetTagHandler.class);
		bindHandler(GetTagSuggestions.class, GetTagSuggestionsHandler.class);
	}

	@Provides
	@Singleton
	@Inject
	Mapper getMapper(DozerBeanMapper mapper) {
		List<String> mappingFiles = ImmutableList
				.of("META-INF/bean-mapping.xml");
		mapper.setMappingFiles(mappingFiles);
		return mapper;
	}
}
