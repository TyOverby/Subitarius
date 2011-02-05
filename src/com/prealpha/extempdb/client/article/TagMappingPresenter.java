/*
 * TagMappingPresenter.java
 * Copyright (C) 2011 Meyer Kizner
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
import com.prealpha.dispatch.shared.DispatcherAsync;
import com.prealpha.extempdb.client.Presenter;
import com.prealpha.extempdb.client.SessionManager;
import com.prealpha.extempdb.client.error.ManagedCallback;
import com.prealpha.extempdb.client.event.ActiveUserEvent;
import com.prealpha.extempdb.client.event.ActiveUserHandler;
import com.prealpha.extempdb.shared.action.AddMappingAction;
import com.prealpha.extempdb.shared.action.MutationResult;
import com.prealpha.extempdb.shared.dto.TagMappingActionDto.Type;
import com.prealpha.extempdb.shared.dto.TagMappingDto;
import com.prealpha.extempdb.shared.dto.TagMappingDto.State;
import com.prealpha.extempdb.shared.dto.UserDto;

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

		eventBus.addHandler(ActiveUserEvent.getType(), new ActiveUserHandler() {
			@Override
			public void activeUserChanged(ActiveUserEvent event) {
				updateState(event.getUser());
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
		String tagName = mapping.getTag().getName();
		display.getMappingLabel().setText(tagName);

		sessionManager.getActiveUser(new ManagedCallback<UserDto>() {
			@Override
			public void onSuccess(UserDto user) {
				updateState(user);
			}
		});
	}

	private void updateState(UserDto user) {
		if (user == null) {
			display.setMappingState(null);
		} else {
			display.setMappingState(mapping.getState());
		}
	}

	private void updateMapping(Type type) {
		String sessionId = sessionManager.getSessionId();
		TagMappingDto.Key mappingKey = mapping.getKey();

		AddMappingAction action = new AddMappingAction(sessionId, mappingKey,
				type);
		dispatcher.execute(action, new ManagedCallback<MutationResult>() {
			@Override
			public void onSuccess(MutationResult result) {
				// TODO: on success, we shouldn't need to reload
				Window.Location.reload();
			}
		});
	}
}
