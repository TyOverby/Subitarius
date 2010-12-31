/*
 * TagInputBox.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.client.common;

import static com.google.common.base.Preconditions.*;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.prealpha.extempdb.client.error.ManagedCallback;
import com.prealpha.extempdb.shared.action.GetTag;
import com.prealpha.extempdb.shared.action.GetTagResult;
import com.prealpha.extempdb.shared.dto.TagDto;
import com.prealpha.extempdb.shared.id.TagName;
import com.prealpha.gwt.dispatch.shared.DispatcherAsync;

/*
 * TODO: non-interface methods, and the quirks in this class
 */
public class TagInputBox extends Composite implements HasValue<TagDto> {
	public static interface TagInputUiBinder extends
			UiBinder<Widget, TagInputBox> {
	}

	@UiField(provided = true)
	final SuggestBox nameBox;

	@UiField(provided = true)
	final Widget statusWidget;

	private final LoadingStatusPresenter statusPresenter;

	private final DispatcherAsync dispatcher;

	private final Scheduler scheduler;

	private TagDto tag;

	private LoadingStatus loadingStatus;

	@Inject
	public TagInputBox(TagInputUiBinder uiBinder, SuggestOracle oracle,
			LoadingStatusPresenter statusPresenter, DispatcherAsync dispatcher,
			Scheduler scheduler) {
		this.statusPresenter = statusPresenter;
		this.dispatcher = dispatcher;
		this.scheduler = scheduler;

		nameBox = new SuggestBox(oracle);
		statusWidget = statusPresenter.getDisplay().asWidget();

		initWidget(uiBinder.createAndBindUi(this));

		nameBox.addKeyPressHandler(new NameHandler());
		nameBox.addSelectionHandler(new NameHandler());
		setLoadingStatus(LoadingStatus.NONE);
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
		TagDto oldTag = tag;
		tag = value;

		if (tag == null) {
			nameBox.setValue(null);
			setLoadingStatus(LoadingStatus.NONE);
		} else {
			nameBox.setValue(tag.getName());
			setLoadingStatus(LoadingStatus.LOADED);
		}

		nameBox.setFocus(false);

		if (fireEvents) {
			ValueChangeEvent.fireIfNotEqual(this, oldTag, tag);
		}
	}

	@Override
	public HandlerRegistration addValueChangeHandler(
			ValueChangeHandler<TagDto> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

	public TagDto create() {
		TagDto tag = new TagDto();
		tag.setName(nameBox.getValue());
		return tag;
	}

	public LoadingStatus getLoadingStatus() {
		return loadingStatus;
	}

	private void setLoadingStatus(LoadingStatus loadingStatus) {
		checkNotNull(loadingStatus);
		this.loadingStatus = loadingStatus;
		statusPresenter.bind(loadingStatus);
	}

	private void fetchTag() {
		setLoadingStatus(LoadingStatus.PENDING);
		String name = nameBox.getValue();
		GetTag action = new GetTag(new TagName(name));
		dispatcher.execute(action, new TagCallback(name));
	}

	private class NameHandler implements KeyPressHandler,
			SelectionHandler<SuggestOracle.Suggestion> {
		@Override
		public void onKeyPress(KeyPressEvent event) {
			/*
			 * This one has to be deferred, because the text box's value may not
			 * be updated to reflect the key press when this event fires.
			 */
			scheduler.scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					fetchTag();
				}
			});
		}

		@Override
		public void onSelection(SelectionEvent<SuggestOracle.Suggestion> event) {
			fetchTag();
		}
	}

	private class TagCallback extends ManagedCallback<GetTagResult> {
		private final String expectedName;

		public TagCallback(String expectedName) {
			this.expectedName = expectedName;
		}

		@Override
		public void onSuccess(GetTagResult result) {
			TagDto tag = result.getTag();
			String currentName = nameBox.getValue();

			// the result may be obsolete by the time it arrives
			if (!expectedName.equals(currentName)) {
				return;
			}

			if (tag == null) {
				if (expectedName.isEmpty()) {
					// we weren't looking for a tag in the first place
					setLoadingStatus(LoadingStatus.NONE);
					setValue(null, true);
				} else {
					// the tag wasn't found
					setLoadingStatus(LoadingStatus.NOT_FOUND);
				}
			} else {
				// the tag was found
				setLoadingStatus(LoadingStatus.LOADED);
				setValue(tag, true);
			}
		}
	}
}
