/*
 * ExtempDbInjector.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.client;

import com.google.gwt.inject.client.AsyncProvider;
import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;
import com.prealpha.extempdb.client.article.ArticleModule;
import com.prealpha.extempdb.client.article.ArticlePresenter;
import com.prealpha.extempdb.client.jump.JumpModule;
import com.prealpha.extempdb.client.jump.JumpPresenter;
import com.prealpha.extempdb.client.core.CoreManager;
import com.prealpha.extempdb.client.core.CoreModule;
import com.prealpha.extempdb.client.error.ErrorModule;
import com.prealpha.extempdb.client.error.ErrorPresenter;
import com.prealpha.extempdb.client.hierarchy.HierarchyModule;
import com.prealpha.extempdb.client.hierarchy.HierarchyPresenter;
import com.prealpha.extempdb.client.main.MainModule;
import com.prealpha.extempdb.client.main.MainPresenter;
import com.prealpha.extempdb.client.settings.SettingsModule;
import com.prealpha.extempdb.client.settings.SettingsPresenter;
import com.prealpha.extempdb.client.taginput.TagInputModule;

@GinModules({ ClientModule.class, CoreModule.class, TagInputModule.class,
		ArticleModule.class, ErrorModule.class, HierarchyModule.class,
		JumpModule.class, MainModule.class, SettingsModule.class })
interface ExtempDbInjector extends Ginjector {
	CoreManager getCoreManager();

	AsyncProvider<ArticlePresenter> getArticlePresenter();

	AsyncProvider<ErrorPresenter> getErrorPresenter();

	AsyncProvider<HierarchyPresenter> getHierarchyPresenter();

	AsyncProvider<JumpPresenter> getJumpPresenter();

	AsyncProvider<MainPresenter> getMainPresenter();

	AsyncProvider<SettingsPresenter> getSettingsPresenter();
}