/*
 * MappingInputPresenter.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.client.article;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.user.client.ui.IsWidget;
import com.prealpha.extempdb.client.Presenter;
import com.prealpha.extempdb.shared.dto.TagDto;
import com.prealpha.extempdb.shared.dto.UserSessionDto;

class MappingInputPresenter implements Presenter<UserSessionDto> {
	static interface Display extends IsWidget {
		DisplayState getDisplayState();

		void setDisplayState(DisplayState state);

		HasValueChangeHandlers<TagDto> getMappingInput();

		HasClickHandlers getAddButton();

		HasClickHandlers getSubmitButton();
	}
}
