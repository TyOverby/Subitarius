/*
 * TagMappingWidget.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.client.article;

import java.util.Collections;
import java.util.List;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.prealpha.extempdb.client.AppPlace;
import com.prealpha.extempdb.client.AppState;
import com.prealpha.extempdb.client.common.CommonResources;
import com.prealpha.extempdb.shared.dto.TagMappingDto.State;

public class TagMappingWidget extends Composite implements
		TagMappingPresenter.Display {
	public static interface TagMappingUiBinder extends
			UiBinder<Widget, TagMappingWidget> {
	}

	@UiField
	Hyperlink mappingLink;

	@UiField
	FocusWidget patrolLink;

	@UiField
	FocusWidget removeLink;

	@Inject
	public TagMappingWidget(TagMappingUiBinder uiBinder,
			CommonResources resources) {
		initWidget(uiBinder.createAndBindUi(this));

		Image patrolImage = new Image(resources.loadedIcon());
		Image removeImage = new Image(resources.notFoundIcon());

		patrolLink.getElement().appendChild(patrolImage.getElement());
		removeLink.getElement().appendChild(removeImage.getElement());
	}

	@Override
	public HasText getMappingLabel() {
		return new HasText() {
			@Override
			public String getText() {
				return mappingLink.getText();
			}

			@Override
			public void setText(String text) {
				List<String> parameters = Collections.singletonList(text);
				AppState appState = new AppState(AppPlace.BROWSE, parameters);
				mappingLink.setTargetHistoryToken(appState.toString());
				mappingLink.setText(text);
			}
		};
	}

	@Override
	public HasClickHandlers getPatrolLink() {
		return patrolLink;
	}

	@Override
	public HasClickHandlers getRemoveLink() {
		return removeLink;
	}

	@Override
	public void setMappingState(State state) {
		if (state == null) {
			patrolLink.setVisible(false);
			removeLink.setVisible(false);
		} else {
			switch (state) {
			case PATROLLED:
				patrolLink.setVisible(false);
				removeLink.setVisible(true);
				break;
			case UNPATROLLED:
				patrolLink.setVisible(true);
				removeLink.setVisible(true);
				break;
			case REMOVED:
				patrolLink.setVisible(true);
				removeLink.setVisible(false);
				break;
			default:
				throw new IllegalStateException();
			}
		}
	}
}
