/*
 * MappingInputPresenter.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.client.article;

import static com.google.common.base.Preconditions.*;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.prealpha.dispatch.shared.DispatcherAsync;
import com.prealpha.extempdb.client.Presenter;
import com.prealpha.extempdb.client.SessionManager;
import com.prealpha.extempdb.client.error.ManagedCallback;
import com.prealpha.extempdb.client.event.SessionEvent;
import com.prealpha.extempdb.client.event.SessionHandler;
import com.prealpha.extempdb.shared.action.AddMapping;
import com.prealpha.extempdb.shared.action.MutationResult;
import com.prealpha.extempdb.shared.dto.ArticleDto;
import com.prealpha.extempdb.shared.dto.TagDto;
import com.prealpha.extempdb.shared.id.UserSessionToken;

public class MappingInputPresenter implements Presenter<ArticleDto> {
	public static interface Display extends IsWidget {
		DisplayState getDisplayState();

		void setDisplayState(DisplayState displayState);

		HasValue<TagDto> getMappingInput();

		HasClickHandlers getAddButton();

		HasClickHandlers getSubmitButton();
	}

	static enum DisplayState {
		READY, PENDING, NO_PERMISSION;
	}

	private final Display display;

	private final DispatcherAsync dispatcher;

	private final SessionManager sessionManager;

	private final ArticleMessages messages;

	private ArticleDto article;

	@Inject
	public MappingInputPresenter(final Display display,
			DispatcherAsync dispatcher, SessionManager sessionManager,
			ArticleMessages messages, EventBus eventBus) {
		this.display = display;
		this.dispatcher = dispatcher;
		this.sessionManager = sessionManager;
		this.messages = messages;

		display.getAddButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				add();
			}
		});

		display.getSubmitButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				submit();
			}
		});

		eventBus.addHandler(SessionEvent.getType(), new SessionHandler() {
			@Override
			public void sessionUpdated(SessionEvent event) {
				if (event.getSession() == null) {
					display.setDisplayState(DisplayState.NO_PERMISSION);
				} else if (display.getDisplayState().equals(
						DisplayState.NO_PERMISSION)) {
					display.setDisplayState(DisplayState.READY);
				}
			}
		});
		sessionManager.fireCurrentSession();
	}

	@Override
	public Display getDisplay() {
		return display;
	}

	@Override
	public void bind(ArticleDto article) {
		checkNotNull(article);
		this.article = article;
	}

	private void add() {
		display.setDisplayState(DisplayState.PENDING);
	}

	private void submit() {
		TagDto tag = display.getMappingInput().getValue();

		if (tag != null) {
			UserSessionToken sessionToken = sessionManager.getSessionToken();
			AddMapping action = new AddMapping(tag, article, sessionToken);
			dispatcher.execute(action, new ManagedCallback<MutationResult>() {
				@Override
				public void onSuccess(MutationResult result) {
					switch (result) {
					case INVALID_REQUEST:
						Window.alert(messages.mappingAlreadyExists());
						break;
					default:
						// TODO: should be some better way to do this
						Window.Location.reload();
						break;
					}
				}
			});
		}
	}
}
