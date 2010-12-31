/*
 * MainInjector.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.client.main;

import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;

@GinModules({ MainModule.class })
public interface MainInjector extends Ginjector {
	MainPresenter getMainManager();
}
