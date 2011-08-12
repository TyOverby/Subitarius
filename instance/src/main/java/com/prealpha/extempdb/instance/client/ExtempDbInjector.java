/*
 * ExtempDbInjector.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.client;

import com.google.gwt.inject.client.AsyncProvider;
import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;
import com.prealpha.extempdb.instance.client.article.ArticleModule;
import com.prealpha.extempdb.instance.client.article.ArticlePresenter;
import com.prealpha.extempdb.instance.client.core.CoreManager;
import com.prealpha.extempdb.instance.client.error.ErrorModule;
import com.prealpha.extempdb.instance.client.error.ErrorPresenter;
import com.prealpha.extempdb.instance.client.hierarchy.HierarchyModule;
import com.prealpha.extempdb.instance.client.hierarchy.HierarchyPresenter;
import com.prealpha.extempdb.instance.client.jump.JumpModule;
import com.prealpha.extempdb.instance.client.jump.JumpPresenter;
import com.prealpha.extempdb.instance.client.main.MainModule;
import com.prealpha.extempdb.instance.client.main.MainPresenter;
import com.prealpha.extempdb.instance.client.taginput.TagInputModule;

@GinModules({ ClientModule.class, TagInputModule.class, ArticleModule.class,
		ErrorModule.class, HierarchyModule.class, JumpModule.class,
		MainModule.class })
interface ExtempDbInjector extends Ginjector {
	CoreManager getCoreManager();

	AsyncProvider<ArticlePresenter> getArticlePresenter();

	AsyncProvider<ErrorPresenter> getErrorPresenter();

	AsyncProvider<HierarchyPresenter> getHierarchyPresenter();

	AsyncProvider<JumpPresenter> getJumpPresenter();

	AsyncProvider<MainPresenter> getMainPresenter();
}
