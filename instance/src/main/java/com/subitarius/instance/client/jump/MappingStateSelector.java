/*
 * MappingStateSelector.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.instance.client.jump;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.subitarius.instance.shared.dto.TagMappingDto.State;

public class MappingStateSelector extends Composite implements
		HasValue<Set<State>> {
	public static interface MappingStateSelectorUiBinder extends
			UiBinder<Widget, MappingStateSelector> {
	}

	@UiField
	HasValue<Boolean> patrolledBox;

	@UiField
	HasValue<Boolean> unpatrolledBox;

	@UiField
	HasValue<Boolean> removedBox;

	@Inject
	public MappingStateSelector(MappingStateSelectorUiBinder uiBinder) {
		initWidget(uiBinder.createAndBindUi(this));

		ValueChangeHandler<Boolean> handler = new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				ValueChangeEvent.fire(MappingStateSelector.this, getValue());
			}
		};

		patrolledBox.addValueChangeHandler(handler);
		unpatrolledBox.addValueChangeHandler(handler);
		removedBox.addValueChangeHandler(handler);
	}

	@Override
	public Set<State> getValue() {
		Set<State> value = new HashSet<State>();

		if (patrolledBox.getValue()) {
			value.add(State.PATROLLED);
		}

		if (unpatrolledBox.getValue()) {
			value.add(State.UNPATROLLED);
		}

		if (removedBox.getValue()) {
			value.add(State.REMOVED);
		}

		return value;
	}

	@Override
	public void setValue(Set<State> value) {
		setValue(value, false);
	}

	@Override
	public void setValue(Set<State> value, boolean fireEvents) {
		Set<State> oldValue = getValue();

		patrolledBox.setValue(value.contains(State.PATROLLED));
		unpatrolledBox.setValue(value.contains(State.UNPATROLLED));
		removedBox.setValue(value.contains(State.REMOVED));

		if (fireEvents) {
			ValueChangeEvent.fireIfNotEqual(this, oldValue, getValue());
		}
	}

	@Override
	public HandlerRegistration addValueChangeHandler(
			ValueChangeHandler<Set<State>> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}
}
