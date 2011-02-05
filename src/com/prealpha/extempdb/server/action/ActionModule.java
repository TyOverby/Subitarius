/*
 * ActionModule.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server.action;

import java.util.List;

import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.matcher.Matcher;
import com.google.inject.matcher.Matchers;
import com.prealpha.dispatch.server.ActionHandler;
import com.prealpha.dispatch.server.ActionHandlerModule;
import com.prealpha.extempdb.shared.action.AddMapping;
import com.prealpha.extempdb.shared.action.AddMappingAction;
import com.prealpha.extempdb.shared.action.ChangePassword;
import com.prealpha.extempdb.shared.action.GetArticle;
import com.prealpha.extempdb.shared.action.GetHierarchy;
import com.prealpha.extempdb.shared.action.GetMapping;
import com.prealpha.extempdb.shared.action.GetMappingsByArticle;
import com.prealpha.extempdb.shared.action.GetMappingsByTag;
import com.prealpha.extempdb.shared.action.GetParagraphs;
import com.prealpha.extempdb.shared.action.GetPoints;
import com.prealpha.extempdb.shared.action.GetTag;
import com.prealpha.extempdb.shared.action.GetTagSuggestions;
import com.prealpha.extempdb.shared.action.GetUser;
import com.prealpha.extempdb.shared.action.LogIn;
import com.prealpha.extempdb.shared.action.LogOut;
import com.prealpha.extempdb.shared.action.UpdateTag;

public class ActionModule extends ActionHandlerModule {
	public ActionModule() {
	}

	@Override
	protected void configure() {
		bindHandler(AddMapping.class, AddMappingHandler.class);
		bindHandler(AddMappingAction.class, AddMappingActionHandler.class);
		bindHandler(ChangePassword.class, ChangePasswordHandler.class);
		bindHandler(GetArticle.class, GetArticleHandler.class);
		bindHandler(GetHierarchy.class, GetHierarchyHandler.class);
		bindHandler(GetMapping.class, GetMappingHandler.class);
		bindHandler(GetMappingsByArticle.class,
				GetMappingsByArticleHandler.class);
		bindHandler(GetMappingsByTag.class, GetMappingsByTagHandler.class);
		bindHandler(GetParagraphs.class, GetParagraphsHandler.class);
		bindHandler(GetPoints.class, GetPointsHandler.class);
		bindHandler(GetTag.class, GetTagHandler.class);
		bindHandler(GetTagSuggestions.class, GetTagSuggestionsHandler.class);
		bindHandler(GetUser.class, GetUserHandler.class);
		bindHandler(LogIn.class, LogInHandler.class);
		bindHandler(LogOut.class, LogOutHandler.class);
		bindHandler(UpdateTag.class, UpdateTagHandler.class);

		@SuppressWarnings("rawtypes")
		Matcher<Class> classMatcher = Matchers.inPackage(
				getClass().getPackage()).and(
				Matchers.subclassesOf(ActionHandler.class));
		SessionValidator interceptor = new SessionValidator();
		requestInjection(interceptor);
		bindInterceptor(classMatcher, Matchers.any(), interceptor);
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
