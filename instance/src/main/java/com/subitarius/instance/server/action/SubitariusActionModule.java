/*
 * SubitariusActionModule.java
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
import com.prealpha.dispatch.server.ActionModule;
import com.subitarius.action.AddArticle;
import com.subitarius.action.AddMapping;
import com.subitarius.action.GetArticleByHash;
import com.subitarius.action.GetArticleByUrl;
import com.subitarius.action.GetHierarchy;
import com.subitarius.action.GetMapping;
import com.subitarius.action.GetMappingsByArticle;
import com.subitarius.action.GetMappingsByTag;
import com.subitarius.action.GetParagraphs;
import com.subitarius.action.GetTag;
import com.subitarius.action.GetTagSuggestions;

public final class SubitariusActionModule extends ActionModule {
	public SubitariusActionModule() {
	}

	@Override
	protected void configureActions() {
		bindAction(AddArticle.class).to(AddArticleHandler.class);
		bindAction(AddMapping.class).to(AddMappingHandler.class);
		bindAction(GetArticleByHash.class).to(GetArticleByHashHandler.class);
		bindAction(GetArticleByUrl.class).to(GetArticleByUrlHandler.class);
		bindAction(GetHierarchy.class).to(GetHierarchyHandler.class);
		bindAction(GetMapping.class).to(GetMappingHandler.class);
		bindAction(GetMappingsByArticle.class).to(
				GetMappingsByArticleHandler.class);
		bindAction(GetMappingsByTag.class).to(GetMappingsByTagHandler.class);
		bindAction(GetParagraphs.class).to(GetParagraphsHandler.class);
		bindAction(GetTag.class).to(GetTagHandler.class);
		bindAction(GetTagSuggestions.class).to(GetTagSuggestionsHandler.class);
	}

	@Provides
	@Singleton
	@Inject
	Mapper getMapper(DozerBeanMapper mapper) {
		List<String> mappingFiles = ImmutableList.of("bean-mapping.xml");
		mapper.setMappingFiles(mappingFiles);
		return mapper;
	}
}
