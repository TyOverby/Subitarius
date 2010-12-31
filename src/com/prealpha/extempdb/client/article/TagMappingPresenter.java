/*
 * TagMappingPresenter.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.client.article;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.prealpha.extempdb.client.Presenter;
import com.prealpha.extempdb.client.SessionManager;
import com.prealpha.extempdb.client.error.ManagedCallback;
import com.prealpha.extempdb.client.event.SessionEvent;
import com.prealpha.extempdb.client.event.SessionHandler;
import com.prealpha.extempdb.shared.action.AddMappingAction;
import com.prealpha.extempdb.shared.action.MutationResult;
import com.prealpha.extempdb.shared.dto.TagMappingActionDto;
import com.prealpha.extempdb.shared.dto.TagMappingActionDto.Type;
import com.prealpha.extempdb.shared.dto.TagMappingDto;
import com.prealpha.extempdb.shared.dto.TagMappingDto.State;
import com.prealpha.extempdb.shared.dto.UserSessionDto;
import com.prealpha.extempdb.shared.id.UserSessionToken;
import com.prealpha.gwt.dispatch.shared.DispatcherAsync;

public class TagMappingPresenter implements Presenter<TagMappingDto> {
	public static interface Display extends IsWidget {
		HasText getMappingLabel();

		HasClickHandlers getPatrolLink();

		HasClickHandlers getRemoveLink();

		void setMappingState(State state);
	}

	private final Display display;

	private final DispatcherAsync dispatcher;

	private final SessionManager sessionManager;

	private TagMappingDto mapping;

	@Inject
	public TagMappingPresenter(Display display, DispatcherAsync dispatcher,
			SessionManager sessionManager, EventBus eventBus) {
		this.display = display;
		this.dispatcher = dispatcher;
		this.sessionManager = sessionManager;

		eventBus.addHandler(SessionEvent.getType(), new SessionHandler() {
			@Override
			public void sessionUpdated(SessionEvent event) {
				updateState(event.getSession());
			}
		});

		display.getPatrolLink().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				updateMapping(Type.PATROL);
			}
		});

		display.getRemoveLink().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				updateMapping(Type.REMOVE);
			}
		});
	}

	@Override
	public Display getDisplay() {
		return display;
	}

	@Override
	public void bind(TagMappingDto mapping) {
		this.mapping = mapping;
		display.getMappingLabel().setText(mapping.getTag().getName());

		sessionManager.getSession(new ManagedCallback<UserSessionDto>() {
			@Override
			public void onSuccess(UserSessionDto session) {
				updateState(session);
			}
		});
	}

	private void updateState(UserSessionDto session) {
		if (session == null) {
			display.setMappingState(null);
		} else {
			display.setMappingState(mapping.getState());
		}
	}

	private void updateMapping(Type type) {
		TagMappingActionDto mappingAction = new TagMappingActionDto();
		mappingAction.setMapping(mapping);
		mappingAction.setType(type);

		UserSessionToken sessionToken = sessionManager.getSessionToken();
		AddMappingAction action = new AddMappingAction(mappingAction,
				sessionToken);
		dispatcher.execute(action, new ManagedCallback<MutationResult>() {
			@Override
			public void onSuccess(MutationResult result) {
				// TODO: on success, we shouldn't need to reload
				Window.Location.reload();
			}
		});
	}
}
