/*
 * PlacePresenter.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.instance.client;

import java.util.List;

public interface PlacePresenter extends Presenter<List<String>> {
	void init();

	/**
	 * Updates the place to account for new parameters in the {@link AppState}.
	 * The parameters list will never be {@code null}, but it may be empty; the
	 * parameters themselves are also guaranteed to be non-{@code null}, and
	 * they will not contain semicolons. No other validity checks are made prior
	 * to calling this method.
	 * 
	 * @param parameters
	 *            the parameters to the {@code AppState}
	 * @throws IllegalArgumentException
	 *             if the parameters represent an invalid {@code AppState}
	 */
	@Override
	void bind(List<String> parameters);

	void destroy();
}
