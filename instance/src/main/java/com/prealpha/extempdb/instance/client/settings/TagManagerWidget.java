/*
 * TagManagerWidget.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.client.settings;

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
import com.prealpha.extempdb.instance.client.taginput.TagInputWidget;
import com.prealpha.extempdb.instance.client.taginput.TagSelector;
import com.prealpha.extempdb.instance.shared.dto.TagDto;

public final class TagManagerWidget extends Composite implements
		TagManagerPresenter.Display {
	public static interface TagManagerUiBinder extends
			UiBinder<Widget, TagManagerWidget> {
	}

	@UiField(provided = true)
	final TagInputWidget tagInput;

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

	@Inject
	private TagManagerWidget(TagManagerUiBinder uiBinder,
			TagInputWidget tagInput, MultiTagInputBox parentInput) {
		this.tagInput = tagInput;
		this.parentInput = parentInput;
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public TagSelector getTagInput() {
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
