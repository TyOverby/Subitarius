/*
 * TagInputPresenter.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.client.taginput;

import static com.google.common.base.Preconditions.*;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.prealpha.dispatch.shared.DispatcherAsync;
import com.prealpha.extempdb.client.Presenter;
import com.prealpha.extempdb.client.error.ManagedCallback;
import com.prealpha.extempdb.shared.action.GetTag;
import com.prealpha.extempdb.shared.action.GetTagResult;
import com.prealpha.extempdb.shared.dto.TagDto;

public class TagInputPresenter implements Presenter<TagDto>,
		HasValueChangeHandlers<LoadingStatus> {
	public static interface Display extends IsWidget, HasValue<String> {
		Presenter<LoadingStatus> getStatusPresenter();
	}

	private final Display display;

	private final DispatcherAsync dispatcher;

	private final HandlerManager handlerManager;

	private List<AsyncCallback<? super TagDto>> callbacks;

	private TagDto tag;

	private LoadingStatus loadingStatus;

	@Inject
	public TagInputPresenter(Display display, DispatcherAsync dispatcher) {
		this.display = display;
		this.dispatcher = dispatcher;
		handlerManager = new HandlerManager(this);

		display.addValueChangeHandler(new TagChangeHandler());
		setLoadingStatus(LoadingStatus.NONE);
	}

	@Override
	public Display getDisplay() {
		return display;
	}

	public TagDto getTag() {
		checkState(loadingStatus.isLoaded());
		return tag;
	}
	
	public String getTagName() {
		checkState(loadingStatus.isLoaded());
		return display.getValue();
	}

	@Override
	public void bind(TagDto tag) {
		this.tag = tag;

		if (tag == null) {
			display.setValue(null);
			setLoadingStatus(LoadingStatus.NONE);
		} else {
			display.setValue(tag.getName());
			setLoadingStatus(LoadingStatus.LOADED);
		}
	}

	@Override
	public HandlerRegistration addValueChangeHandler(
			ValueChangeHandler<LoadingStatus> handler) {
		return handlerManager.addHandler(ValueChangeEvent.getType(), handler);
	}

	@Override
	public void fireEvent(GwtEvent<?> event) {
		handlerManager.fireEvent(event);
	}

	public LoadingStatus getLoadingStatus() {
		return loadingStatus;
	}

	private void setLoadingStatus(LoadingStatus loadingStatus) {
		LoadingStatus oldStatus = this.loadingStatus;
		this.loadingStatus = loadingStatus;
		display.getStatusPresenter().bind(loadingStatus);

		if ((oldStatus == null || oldStatus.isLoaded())
				&& !loadingStatus.isLoaded()) {
			callbacks = new ArrayList<AsyncCallback<? super TagDto>>();
		} else if (!oldStatus.isLoaded() && loadingStatus.isLoaded()) {
			for (AsyncCallback<? super TagDto> callback : callbacks) {
				callback.onSuccess(tag);
			}
			callbacks = null;
		}

		/*
		 * No check for equality prior to firing. This ensures that an event
		 * fires for every time the user might have changed the tag. Because of
		 * this, clients can use ValueChangeHandler to monitor changes not just
		 * in the loading status, but also in the tag itself.
		 * 
		 * TODO: document this behavior properly
		 */
		ValueChangeEvent.fire(this, loadingStatus);
	}

	private final class TagChangeHandler implements ValueChangeHandler<String> {
		@Override
		public void onValueChange(ValueChangeEvent<String> event) {
			String tagName = event.getValue();

			if (tagName == null || tagName.isEmpty()) {
				bind(null); // sets LoadingStatus.NONE
				return;
			}

			GetTag action = new GetTag(tagName);
			dispatcher.execute(action, new TagCallback(tagName));

			// this will be changed when the callback fires
			setLoadingStatus(LoadingStatus.PENDING);
		}
	}

	private final class TagCallback extends ManagedCallback<GetTagResult> {
		private final String expectedName;

		public TagCallback(String expectedName) {
			checkNotNull(expectedName);
			checkArgument(!expectedName.isEmpty());
			this.expectedName = expectedName;
		}

		@Override
		public void onSuccess(GetTagResult result) {
			TagDto tag = result.getTag();
			String currentName = display.getValue();

			// the result may be obsolete by the time it arrives
			if (!expectedName.equals(currentName)) {
				return;
			}

			if (tag == null) {
				setLoadingStatus(LoadingStatus.NOT_FOUND);
			} else {
				bind(tag); // sets LoadingStatus.LOADED
			}
		}
	}
}
