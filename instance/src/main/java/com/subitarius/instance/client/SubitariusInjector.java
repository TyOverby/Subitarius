/*
 * SubitariusInjector.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.instance.client;

import com.google.gwt.inject.client.AsyncProvider;
import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;
import com.subitarius.instance.client.article.ArticleModule;
import com.subitarius.instance.client.article.ArticlePresenter;
import com.subitarius.instance.client.core.CoreManager;
import com.subitarius.instance.client.error.ErrorModule;
import com.subitarius.instance.client.error.ErrorPresenter;
import com.subitarius.instance.client.hierarchy.HierarchyModule;
import com.subitarius.instance.client.hierarchy.HierarchyPresenter;
import com.subitarius.instance.client.jump.JumpModule;
import com.subitarius.instance.client.jump.JumpPresenter;
import com.subitarius.instance.client.main.MainModule;
import com.subitarius.instance.client.main.MainPresenter;
import com.subitarius.instance.client.taginput.TagInputModule;

@GinModules({ ClientModule.class, TagInputModule.class, ArticleModule.class,
		ErrorModule.class, HierarchyModule.class, JumpModule.class,
		MainModule.class })
interface SubitariusInjector extends Ginjector {
	CoreManager getCoreManager();

	AsyncProvider<ArticlePresenter> getArticlePresenter();

	AsyncProvider<ErrorPresenter> getErrorPresenter();

	AsyncProvider<HierarchyPresenter> getHierarchyPresenter();

	AsyncProvider<JumpPresenter> getJumpPresenter();

	AsyncProvider<MainPresenter> getMainPresenter();
}
