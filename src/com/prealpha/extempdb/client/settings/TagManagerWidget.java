/*
 * TagManagerWidget.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.client.settings;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.prealpha.extempdb.client.common.TagInputBox;

public class TagManagerWidget extends Composite implements
		TagManagerPresenter.Display {
	public static interface TagManagerUiBinder extends
			UiBinder<Widget, TagManagerWidget> {
	}

	@UiField(provided = true)
	final TagInputBox tagInput;

	@UiField
	HasValue<Boolean> searchedBox;

	@UiField(provided = true)
	final MultiTagInputBox parentInput;

	@UiField
	FocusWidget saveButton;

	@UiField
	FocusWidget deleteButton;

	@UiField
	HasClickHandlers resetButton;

	@UiField
	HasText errorLabel;

	@Inject
	public TagManagerWidget(TagManagerUiBinder uiBinder, TagInputBox tagInput,
			MultiTagInputBox parentInput) {
		this.tagInput = tagInput;
		this.parentInput = parentInput;

		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public TagInputBox getTagInput() {
		return tagInput;
	}

	@Override
	public HasValue<Boolean> getSearchedBox() {
		return searchedBox;
	}

	@Override
	public MultiTagInputBox getParentInput() {
		return parentInput;
	}

	@Override
	public HasClickHandlers getSaveButton() {
		return saveButton;
	}

	@Override
	public HasClickHandlers getDeleteButton() {
		return deleteButton;
	}

	@Override
	public HasClickHandlers getResetButton() {
		return resetButton;
	}

	@Override
	public HasText getErrorLabel() {
		return errorLabel;
	}
}
