/*
 * PlacePresenter.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.client;

import java.util.List;

public interface PlacePresenter extends Presenter<List<String>> {
	void init();

	void destroy();
}
