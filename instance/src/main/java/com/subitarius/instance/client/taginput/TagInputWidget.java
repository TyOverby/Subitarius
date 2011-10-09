/*
 * TagInputWidget.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.instance.client.taginput;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.prealpha.dispatch.shared.DispatcherAsync;
import com.subitarius.action.GetTag;
import com.subitarius.action.GetTagResult;
import com.subitarius.action.dto.TagDto;
import com.subitarius.instance.client.error.ManagedCallback;

public final class TagInputWidget extends Composite implements TagSelector {
	public static interface TagInputUiBinder extends
			UiBinder<Widget, TagInputWidget> {
	}

	@UiField(provided = true)
	final SuggestBox nameBox;

	@UiField(provided = true)
	final LoadingStatusWidget statusWidget;

	private final DispatcherAsync dispatcher;

	private final Scheduler scheduler;

	private TagDto tag;

	@Inject
	private TagInputWidget(TagInputUiBinder uiBinder,
			DispatcherAsync dispatcher, Scheduler scheduler,
			SuggestBox nameBox, LoadingStatusWidget statusWidget) {
		this.dispatcher = dispatcher;
		this.scheduler = scheduler;
		this.nameBox = nameBox;
		this.statusWidget = statusWidget;
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public TagDto getValue() {
		return tag;
	}

	@Override
	public void setValue(TagDto value) {
		setValue(value, false);
	}

	@Override
	public void setValue(TagDto value, boolean fireEvents) {
		if (value == null) {
			statusWidget.setValue(LoadingStatus.NONE, true);
		} else {
			statusWidget.setValue(LoadingStatus.LOADED, true);
		}
		nameBox.setText((value == null) ? null : value.getName());
		setValueImpl(value, fireEvents);
	}

	private void setValueImpl(TagDto value, boolean fireEvents) {
		TagDto oldValue = tag;
		tag = value;
		if (fireEvents) {
			ValueChangeEvent.fireIfNotEqual(this, oldValue, value);
		}
	}

	@Override
	public HandlerRegistration addValueChangeHandler(
			ValueChangeHandler<TagDto> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

	@Override
	public String getSelectedName() {
		return nameBox.getValue();
	}

	@UiHandler("nameBox")
	void onKeyDown(KeyDownEvent event) {
		// nameBox's value isn't updated when this event fires
		scheduler.scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				requestTag(nameBox.getValue());
			}
		});
	}

	@UiHandler("nameBox")
	void onSelection(SelectionEvent<Suggestion> event) {
		requestTag(event.getSelectedItem().getReplacementString());
	}

	private void requestTag(String tagName) {
		if (tagName != null && !tagName.isEmpty()) {
			GetTag action = new GetTag(tagName);
			dispatcher.execute(action, new TagCallback(tagName));
			statusWidget.setValue(LoadingStatus.PENDING);
			setValueImpl(null, true);
		} else {
			setValue(null, true);
		}
	}

	private final class TagCallback extends ManagedCallback<GetTagResult> {
		private final String expectedName;

		private TagCallback(String expectedName) {
			if (expectedName == null || expectedName.isEmpty()) {
				this.expectedName = null;
			} else {
				this.expectedName = expectedName;
			}
		}

		@Override
		public void onSuccess(GetTagResult result) {
			// this result may be obsolete already
			if (nameBox.getValue() == null) {
				if (expectedName != null) {
					return;
				}
			} else if (!nameBox.getValue().equals(expectedName)) {
				return;
			}

			TagDto tag = result.getTag();
			if (tag == null) {
				if (expectedName == null) {
					statusWidget.setValue(LoadingStatus.NONE);
				} else {
					statusWidget.setValue(LoadingStatus.NOT_FOUND);
				}
			} else {
				statusWidget.setValue(LoadingStatus.LOADED, true);
			}
			setValueImpl(tag, true);
		}
	}
}
