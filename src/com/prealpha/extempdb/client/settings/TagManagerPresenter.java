/*
 * TagManagerPresenter.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.client.settings;

import java.util.HashSet;
import java.util.Set;

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
import com.prealpha.dispatch.shared.DispatcherAsync;
import com.prealpha.extempdb.client.Presenter;
import com.prealpha.extempdb.client.SessionManager;
import com.prealpha.extempdb.client.common.LoadingStatus;
import com.prealpha.extempdb.client.common.TagInputBox;
import com.prealpha.extempdb.client.error.ManagedCallback;
import com.prealpha.extempdb.shared.action.DeleteTag;
import com.prealpha.extempdb.shared.action.MutationResult;
import com.prealpha.extempdb.shared.action.UpdateTag;
import com.prealpha.extempdb.shared.dto.TagDto;

/*
 * TODO: doesn't present anything
 * TODO: relies on TagInputBox directly, so would require GwtTestCase to unit test
 */
public class TagManagerPresenter implements Presenter<Void> {
	public static interface Display extends IsWidget {
		TagInputBox getTagInput();

		HasValue<Boolean> getSearchedBox();

		HasValue<Set<TagDto>> getParentInput();

		HasClickHandlers getUpdateButton();

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

		display.getUpdateButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				update();
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

	private void update() {
		if (display.getParentInput().getValue().size() == 0) {
			if (!Window.confirm(messages.saveNoParent())) {
				return;
			}
		}

		if (isStatusValid(true)) {
			String tagName = display.getTagInput().getTagName();
			boolean searched = display.getSearchedBox().getValue();
			Set<String> parents = new HashSet<String>();
			for (TagDto parentDto : display.getParentInput().getValue()) {
				parents.add(parentDto.getName());
			}

			String sessionId = sessionManager.getSessionId();
			UpdateTag action = new UpdateTag(sessionId, tagName, searched,
					parents);
			dispatcher.execute(action, new ActionCallback());
		}
	}

	private void delete() {
		if (isStatusValid(false)) {
			String sessionId = sessionManager.getSessionId();
			String tagName = display.getTagInput().getTagName();
			DeleteTag action = new DeleteTag(sessionId, tagName);
			dispatcher.execute(action, new ActionCallback());
		}
	}

	private void reset() {
		display.getTagInput().setValue(null, true);
		display.getParentInput().setValue(null, true);
		display.getErrorLabel().setText(null);
	}

	private boolean isStatusValid(boolean notFoundValid) {
		switch (display.getTagInput().getLoadingStatus()) {
		case NOT_FOUND:
			if (notFoundValid) {
				break;
			}
		case NONE:
			display.getErrorLabel().setText(messages.noTagInput());
			return false;
		case PENDING:
			display.getErrorLabel().setText(messages.noTagLoaded());
			return false;
		}

		display.getErrorLabel().setText(null);
		return true;
	}

	private static class ActionCallback extends ManagedCallback<MutationResult> {
		@Override
		public void onSuccess(MutationResult result) {
			Window.Location.reload();
		}
	}
}
