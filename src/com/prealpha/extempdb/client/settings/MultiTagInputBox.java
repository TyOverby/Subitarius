/*
 * MultiTagInputBox.java
 * Copyright (C) 2011 Meyer Kizner
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
import com.prealpha.extempdb.client.taginput.LoadingStatus;
import com.prealpha.extempdb.client.taginput.TagInputPresenter;
import com.prealpha.extempdb.shared.dto.TagDto;

public class MultiTagInputBox extends Composite implements
		HasValue<Set<TagDto>> {
	private final Provider<TagInputPresenter> inputPresenterProvider;

	private final List<TagInputPresenter> inputPresenters;

	private final Panel panel;

	private Set<TagDto> tags;

	@Inject
	public MultiTagInputBox(Provider<TagInputPresenter> inputPresenterProvider) {
		this.inputPresenterProvider = inputPresenterProvider;
		inputPresenters = new ArrayList<TagInputPresenter>();
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
		inputPresenters.clear();

		for (TagDto tag : tags) {
			TagInputPresenter inputPresenter = inputPresenterProvider.get();
			inputPresenter.bind(tag);
			inputPresenter.addValueChangeHandler(new TagInputHandler());
			inputPresenters.add(inputPresenter);
		}

		TagInputPresenter emptyInputPresenter = inputPresenterProvider.get();
		emptyInputPresenter.addValueChangeHandler(new TagInputHandler());
		inputPresenters.add(emptyInputPresenter);
	}

	private void updatePanel() {
		panel.clear();

		for (TagInputPresenter inputPresenter : inputPresenters) {
			panel.add(inputPresenter.getDisplay().asWidget());
		}
	}

	private class TagInputHandler implements ValueChangeHandler<LoadingStatus> {
		@Override
		public void onValueChange(ValueChangeEvent<LoadingStatus> event) {
			Set<TagDto> updatedSet = new HashSet<TagDto>();

			for (TagInputPresenter inputPresenter : inputPresenters) {
				if (inputPresenter.getLoadingStatus().equals(
						LoadingStatus.LOADED)) {
					updatedSet.add(inputPresenter.getTag());
				}
			}

			setValue(updatedSet, true);
		}
	}
}
