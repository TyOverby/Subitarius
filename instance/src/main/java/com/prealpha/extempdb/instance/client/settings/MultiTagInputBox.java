/*
 * MultiTagInputBox.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.client.settings;

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
import com.prealpha.extempdb.instance.client.taginput.TagInputWidget;
import com.prealpha.extempdb.instance.shared.dto.TagDto;

public final class MultiTagInputBox extends Composite implements
		HasValue<Set<TagDto>> {
	private final Provider<TagInputWidget> tagInputProvider;

	private final List<TagInputWidget> tagInputs;

	private final Panel panel;

	private Set<TagDto> tags;

	@Inject
	private MultiTagInputBox(Provider<TagInputWidget> tagInputProvider) {
		this.tagInputProvider = tagInputProvider;
		tagInputs = new ArrayList<TagInputWidget>();
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

		if (!tags.equals(oldTags)) {
			updateInputBoxes();
			updatePanel();
		}

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
		tagInputs.clear();

		for (TagDto tag : tags) {
			TagInputWidget tagInput = tagInputProvider.get();
			tagInput.setValue(tag);
			tagInput.addValueChangeHandler(new TagInputHandler());
			tagInputs.add(tagInput);
		}

		TagInputWidget emptyTagInput = tagInputProvider.get();
		emptyTagInput.addValueChangeHandler(new TagInputHandler());
		tagInputs.add(emptyTagInput);
	}

	private void updatePanel() {
		panel.clear();
		for (TagInputWidget tagInput : tagInputs) {
			panel.add(tagInput);
		}
	}

	private class TagInputHandler implements ValueChangeHandler<TagDto> {
		@Override
		public void onValueChange(ValueChangeEvent<TagDto> event) {
			Set<TagDto> updatedSet = new HashSet<TagDto>();
			for (TagInputWidget tagInput : tagInputs) {
				TagDto tag = tagInput.getValue();
				if (tag != null) {
					updatedSet.add(tag);
				}
			}
			setValue(updatedSet, true);
		}
	}
}
