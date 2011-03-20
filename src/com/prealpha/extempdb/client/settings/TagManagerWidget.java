/*
 * TagManagerWidget.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.client.settings;

import java.util.Set;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.prealpha.extempdb.client.taginput.TagInputPresenter;
import com.prealpha.extempdb.shared.dto.TagDto;

public class TagManagerWidget extends Composite implements
		TagManagerPresenter.Display {
	public static interface TagManagerUiBinder extends
			UiBinder<Widget, TagManagerWidget> {
	}

	@UiField(provided = true)
	final Widget tagInputWidget;

	@UiField
	HasValue<Boolean> searchedBox;

	@UiField(provided = true)
	final MultiTagInputBox parentInput;

	@UiField
	FocusWidget updateButton;

	@UiField
	FocusWidget deleteButton;

	@UiField
	HasClickHandlers resetButton;

	@UiField
	HasText errorLabel;

	private final TagInputPresenter tagInput;

	@Inject
	public TagManagerWidget(TagManagerUiBinder uiBinder,
			TagInputPresenter tagInput, MultiTagInputBox parentInput) {
		this.tagInput = tagInput;
		this.parentInput = parentInput;

		tagInputWidget = tagInput.getDisplay().asWidget();
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public TagInputPresenter getTagInput() {
		return tagInput;
	}

	@Override
	public HasValue<Boolean> getSearchedBox() {
		return searchedBox;
	}

	@Override
	public HasValue<Set<TagDto>> getParentInput() {
		return parentInput;
	}

	@Override
	public HasClickHandlers getUpdateButton() {
		return updateButton;
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
