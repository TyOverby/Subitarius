/*
 * MultiTagInputBox.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.client.settings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.prealpha.extempdb.client.common.LoadingStatus;
import com.prealpha.extempdb.client.common.TagInputBox;
import com.prealpha.extempdb.shared.dto.TagDto;

public class MultiTagInputBox extends Composite implements
		HasValue<Set<TagDto>> {
	private final Provider<TagInputBox> inputBoxProvider;

	private final List<TagInputBox> inputBoxes;

	private final Panel panel;

	private Set<TagDto> tags;

	@Inject
	public MultiTagInputBox(Provider<TagInputBox> inputBoxProvider) {
		this.inputBoxProvider = inputBoxProvider;
		inputBoxes = new ArrayList<TagInputBox>();
		panel = new VerticalPanel();
		setValue(null);

		initWidget(panel);
	}

	@Override
	public Set<TagDto> getValue() {
		return tags;
	}

	@Override
	public void setValue(Set<TagDto> value) {
		setValue(value, false);
	}

	@Override
	public void setValue(Set<TagDto> value, boolean fireEvents) {
		Set<TagDto> oldTags = tags;
		if (value == null) {
			tags = Collections.emptySet();
		} else {
			tags = Collections.unmodifiableSet(value);
		}

		updateInputBoxes();
		updatePanel();

		if (fireEvents) {
			ValueChangeEvent.fireIfNotEqual(this, oldTags, tags);
		}
	}

	@Override
	public HandlerRegistration addValueChangeHandler(
			ValueChangeHandler<Set<TagDto>> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

	private void updateInputBoxes() {
		inputBoxes.clear();

		for (TagDto tag : tags) {
			TagInputBox inputBox = inputBoxProvider.get();
			inputBox.setValue(tag);
			inputBox.addValueChangeHandler(new TagInputHandler());
			inputBoxes.add(inputBox);
		}

		TagInputBox emptyInputBox = inputBoxProvider.get();
		emptyInputBox.addValueChangeHandler(new TagInputHandler());
		inputBoxes.add(emptyInputBox);
	}

	private void updatePanel() {
		panel.clear();

		for (TagInputBox inputBox : inputBoxes) {
			panel.add(inputBox);
		}
	}

	private class TagInputHandler implements ValueChangeHandler<TagDto> {
		@Override
		public void onValueChange(ValueChangeEvent<TagDto> event) {
			Set<TagDto> updatedSet = new HashSet<TagDto>();

			for (TagInputBox inputBox : inputBoxes) {
				if (inputBox.getLoadingStatus().equals(LoadingStatus.LOADED)) {
					updatedSet.add(inputBox.getValue());
				}
			}

			setValue(updatedSet, true);
		}
	}
}
