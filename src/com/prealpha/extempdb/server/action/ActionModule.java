/*
 * ActionModule.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server.action;

import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;

import com.google.inject.Singleton;
import com.prealpha.extempdb.shared.action.AddMappingAction;
import com.prealpha.extempdb.shared.action.ChangePassword;
import com.prealpha.extempdb.shared.action.GetArticle;
import com.prealpha.extempdb.shared.action.GetHierarchy;
import com.prealpha.extempdb.shared.action.GetMapping;
import com.prealpha.extempdb.shared.action.GetMappingsByArticle;
import com.prealpha.extempdb.shared.action.GetMappingsByTag;
import com.prealpha.extempdb.shared.action.GetParagraphs;
import com.prealpha.extempdb.shared.action.GetSession;
import com.prealpha.extempdb.shared.action.GetTag;
import com.prealpha.extempdb.shared.action.GetTagSuggestions;
import com.prealpha.extempdb.shared.action.LogIn;
import com.prealpha.extempdb.shared.action.UpdateTag;
import com.prealpha.gwt.dispatch.server.ActionHandlerModule;

public class ActionModule extends ActionHandlerModule {
	@Override
	protected void configure() {
		bind(Mapper.class).to(DozerBeanMapper.class).in(Singleton.class);

		bindHandler(AddMappingAction.class, AddMappingActionHandler.class);
		bindHandler(ChangePassword.class, ChangePasswordHandler.class);
		bindHandler(GetArticle.class, GetArticleHandler.class);
		bindHandler(GetHierarchy.class, GetHierarchyHandler.class);
		bindHandler(GetMapping.class, GetMappingHandler.class);
		bindHandler(GetMappingsByArticle.class,
				GetMappingsByArticleHandler.class);
		bindHandler(GetMappingsByTag.class, GetMappingsByTagHandler.class);
		bindHandler(GetParagraphs.class, GetParagraphsHandler.class);
		bindHandler(GetSession.class, GetSessionHandler.class);
		bindHandler(GetTag.class, GetTagHandler.class);
		bindHandler(GetTagSuggestions.class, GetTagSuggestionsHandler.class);
		bindHandler(LogIn.class, LogInHandler.class);
		bindHandler(UpdateTag.class, UpdateTagHandler.class);
	}
}
