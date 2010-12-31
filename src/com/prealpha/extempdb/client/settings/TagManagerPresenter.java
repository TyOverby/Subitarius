/*
 * TagManagerPresenter.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.client.settings;

import java.util.HashSet;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.prealpha.extempdb.client.Presenter;
import com.prealpha.extempdb.client.SessionManager;
import com.prealpha.extempdb.client.common.LoadingStatus;
import com.prealpha.extempdb.client.common.TagInputBox;
import com.prealpha.extempdb.client.error.ManagedCallback;
import com.prealpha.extempdb.shared.action.MutationResult;
import com.prealpha.extempdb.shared.action.UpdateTag;
import com.prealpha.extempdb.shared.action.UpdateTag.UpdateType;
import com.prealpha.extempdb.shared.dto.TagDto;
import com.prealpha.extempdb.shared.id.UserSessionToken;
import com.prealpha.gwt.dispatch.shared.DispatcherAsync;

/*
 * TODO: doesn't present anything
 * TODO: relies on Widgets directly, so would require GwtTestCase to unit test
 */
public class TagManagerPresenter implements Presenter<Void> {
	public static interface Display extends IsWidget {
		TagInputBox getTagInput();

		HasValue<Boolean> getSearchedBox();

		MultiTagInputBox getParentInput();

		HasClickHandlers getSaveButton();

		HasClickHandlers getDeleteButton();

		HasClickHandlers getResetButton();

		HasText getErrorLabel();
	}

	private final Display display;

	private final SessionManager sessionManager;

	private final DispatcherAsync dispatcher;

	private final SettingsMessages messages;

	@Inject
	public TagManagerPresenter(Display display, SessionManager sessionManager,
			DispatcherAsync dispatcher, SettingsMessages messages) {
		this.display = display;
		this.sessionManager = sessionManager;
		this.dispatcher = dispatcher;
		this.messages = messages;

		display.getTagInput().addValueChangeHandler(
				new ValueChangeHandler<TagDto>() {
					@Override
					public void onValueChange(ValueChangeEvent<TagDto> event) {
						LoadingStatus loadingStatus = TagManagerPresenter.this.display
								.getTagInput().getLoadingStatus();
						setTagFound(loadingStatus.equals(LoadingStatus.LOADED));
					}
				});

		display.getSaveButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				save();
			}
		});

		display.getDeleteButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				delete();
			}
		});

		display.getResetButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				reset();
			}
		});
	}

	@Override
	public Display getDisplay() {
		return display;
	}

	@Override
	public void bind(Void v) {
	}

	private void setTagFound(boolean tagFound) {
		TagDto tag = display.getTagInput().getValue();

		if (tagFound && tag != null) {
			display.getSearchedBox().setValue(tag.isSearched());
			display.getParentInput().setValue(tag.getParents());
		} else {
			display.getSearchedBox().setValue(true);
			display.getParentInput().setValue(null);
		}
	}

	private void save() {
		if (display.getParentInput().getValue().size() == 0) {
			if (!Window.confirm(messages.saveNoParent())) {
				return;
			}
		}

		doUpdate(UpdateType.SAVE);
	}

	private void delete() {
		doUpdate(UpdateType.DELETE);
	}

	private void reset() {
		display.getTagInput().setValue(null, true);
		display.getParentInput().setValue(null, true);
		display.getErrorLabel().setText(null);
	}

	private void doUpdate(UpdateType updateType) {
		LoadingStatus loadingStatus = display.getTagInput().getLoadingStatus();

		switch (loadingStatus) {
		case NOT_FOUND:
			if (updateType.equals(UpdateType.SAVE)) {
				break;
			}
		case NONE:
			display.getErrorLabel().setText(messages.noTagInput());
			return;
		case PENDING:
			display.getErrorLabel().setText(messages.noTagLoaded());
			return;
		}

		TagDto tag = display.getTagInput().create();
		tag.setSearched(display.getSearchedBox().getValue());
		tag.setParents(display.getParentInput().getValue());

		// workaround for serialization issue
		tag.setParents(new HashSet<TagDto>(tag.getParents()));

		UserSessionToken token = sessionManager.getSessionToken();
		UpdateTag action = new UpdateTag(tag, updateType, token);
		dispatcher.execute(action, new UpdateCallback());

		display.getErrorLabel().setText(null);
	}

	private class UpdateCallback extends ManagedCallback<MutationResult> {
		@Override
		public void onSuccess(MutationResult result) {
			Window.Location.reload();
		}
	}
}
