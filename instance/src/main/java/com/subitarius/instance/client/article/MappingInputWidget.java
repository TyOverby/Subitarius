/*
 * MappingInputWidget.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.instance.client.article;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.subitarius.action.dto.TagDto;
import com.subitarius.instance.client.taginput.TagInputWidget;

public final class MappingInputWidget extends Composite implements
		MappingInputPresenter.Display {
	public static interface MappingInputUiBinder extends
			UiBinder<Widget, MappingInputWidget> {
	}

	@UiField(provided = true)
	final TagInputWidget tagInput;

	@UiField
	FocusWidget submitButton;

	@Inject
	private MappingInputWidget(MappingInputUiBinder uiBinder,
			TagInputWidget tagInput) {
		this.tagInput = tagInput;
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public HasValue<TagDto> getTagInput() {
		return tagInput;
	}

	@Override
	public HasClickHandlers getSubmitButton() {
		return submitButton;
	}
}
