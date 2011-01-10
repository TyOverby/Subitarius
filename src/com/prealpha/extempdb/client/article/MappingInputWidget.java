/*
 * MappingInputWidget.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.client.article;

import static com.google.common.base.Preconditions.*;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.prealpha.extempdb.client.article.MappingInputPresenter.DisplayState;
import com.prealpha.extempdb.client.common.TagInputBox;
import com.prealpha.extempdb.shared.dto.TagDto;

public class MappingInputWidget extends Composite implements
		MappingInputPresenter.Display {
	public static interface MappingInputUiBinder extends
			UiBinder<Widget, MappingInputWidget> {
	}

	@UiField(provided = true)
	final TagInputBox mappingInput;

	@UiField
	FocusWidget addButton;

	@UiField
	FocusWidget submitButton;

	private DisplayState displayState;

	@Inject
	public MappingInputWidget(MappingInputUiBinder uiBinder,
			TagInputBox mappingInput) {
		this.mappingInput = mappingInput;
		displayState = DisplayState.NO_PERMISSION;
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public DisplayState getDisplayState() {
		return displayState;
	}

	@Override
	public void setDisplayState(DisplayState displayState) {
		checkNotNull(displayState);
		this.displayState = displayState;

		switch (displayState) {
		case READY:
			setVisible(true);
			mappingInput.setVisible(false);
			addButton.setEnabled(true);
			submitButton.setEnabled(false);
			break;
		case PENDING:
			setVisible(true);
			mappingInput.setVisible(true);
			addButton.setEnabled(false);
			submitButton.setEnabled(true);
			break;
		case NO_PERMISSION:
			setVisible(false);
			mappingInput.setVisible(false);
			addButton.setEnabled(false);
			submitButton.setEnabled(false);
			break;
		default:
			throw new IllegalStateException();
		}
	}

	@Override
	public HasValue<TagDto> getMappingInput() {
		return mappingInput;
	}

	@Override
	public HasClickHandlers getAddButton() {
		return addButton;
	}

	@Override
	public HasClickHandlers getSubmitButton() {
		return submitButton;
	}
}
