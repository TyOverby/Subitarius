/*
 * TagMappingPresenter.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.client.article;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.prealpha.dispatch.shared.DispatcherAsync;
import com.prealpha.extempdb.instance.client.Presenter;
import com.prealpha.extempdb.instance.client.error.ManagedCallback;
import com.prealpha.extempdb.instance.shared.action.AddMappingAction;
import com.prealpha.extempdb.instance.shared.action.MutationResult;
import com.prealpha.extempdb.instance.shared.dto.TagMappingActionDto.Type;
import com.prealpha.extempdb.instance.shared.dto.TagMappingDto;
import com.prealpha.extempdb.instance.shared.dto.TagMappingDto.State;

public class TagMappingPresenter implements Presenter<TagMappingDto> {
	public static interface Display extends IsWidget {
		HasText getMappingLabel();

		HasClickHandlers getPatrolLink();

		HasClickHandlers getRemoveLink();

		void setMappingState(State state);
	}

	private final Display display;

	private final DispatcherAsync dispatcher;

	private TagMappingDto mapping;

	@Inject
	public TagMappingPresenter(Display display, DispatcherAsync dispatcher,
			EventBus eventBus) {
		this.display = display;
		this.dispatcher = dispatcher;

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
	}

	private void updateMapping(Type type) {
		TagMappingDto.Key mappingKey = mapping.getKey();

		AddMappingAction action = new AddMappingAction(mappingKey, type);
		dispatcher.execute(action, new ManagedCallback<MutationResult>() {
			@Override
			public void onSuccess(MutationResult result) {
				// TODO: on success, we shouldn't need to reload
				Window.Location.reload();
			}
		});
	}
}
