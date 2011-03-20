/*
 * TagInputWidget.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.client.taginput;

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
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.prealpha.extempdb.client.Presenter;

public class TagInputWidget extends Composite implements
		TagInputPresenter.Display {
	public static interface TagInputUiBinder extends
			UiBinder<Widget, TagInputWidget> {
	}

	@UiField(provided = true)
	final SuggestBox nameBox;

	@UiField(provided = true)
	final Widget statusWidget;

	private final LoadingStatusPresenter statusPresenter;

	private final Scheduler scheduler;

	@Inject
	public TagInputWidget(TagInputUiBinder uiBinder, SuggestOracle oracle,
			LoadingStatusPresenter statusPresenter, Scheduler scheduler) {
		this.statusPresenter = statusPresenter;
		this.scheduler = scheduler;

		nameBox = new SuggestBox(oracle);
		statusWidget = statusPresenter.getDisplay().asWidget();

		initWidget(uiBinder.createAndBindUi(this));

		nameBox.addKeyPressHandler(new NameHandler());
		nameBox.addSelectionHandler(new NameHandler());
	}

	@Override
	public String getValue() {
		return nameBox.getValue();
	}

	@Override
	public void setValue(String value) {
		setValue(value, false);
	}

	@Override
	public void setValue(String value, boolean fireEvents) {
		String oldValue = getValue();
		nameBox.setValue(value);

		if (fireEvents) {
			ValueChangeEvent.fireIfNotEqual(this, oldValue, value);
		}
	}

	@Override
	public HandlerRegistration addValueChangeHandler(
			ValueChangeHandler<String> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

	@Override
	public Presenter<LoadingStatus> getStatusPresenter() {
		return statusPresenter;
	}

	private void fireValueChange() {
		ValueChangeEvent.fire(this, nameBox.getText());
	}

	private final class NameHandler implements KeyPressHandler,
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
					fireValueChange();
				}
			});
		}

		@Override
		public void onSelection(SelectionEvent<SuggestOracle.Suggestion> event) {
			fireValueChange();
		}
	}
}
