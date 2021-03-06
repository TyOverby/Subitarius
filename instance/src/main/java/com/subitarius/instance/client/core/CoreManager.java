/*
 * CoreManager.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.instance.client.core;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.inject.client.AsyncProvider;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.inject.Inject;
import com.subitarius.instance.client.AppPlace;
import com.subitarius.instance.client.AppState;
import com.subitarius.instance.client.AppStateEvent;
import com.subitarius.instance.client.AppStateException;
import com.subitarius.instance.client.AppStateHandler;
import com.subitarius.instance.client.CommonResources;
import com.subitarius.instance.client.HistoryManager;
import com.subitarius.instance.client.PlacePresenter;
import com.subitarius.instance.client.error.ManagedCallback;

public final class CoreManager {
	private final CoreWidget coreWidget;

	private final EventBus eventBus;

	private final HistoryManager historyManager;

	private final CommonResources commonResources;

	private final CoreResources coreResources;

	private AppPlace currentPlace;

	private PlacePresenter currentPresenter;

	@Inject
	public CoreManager(CoreWidget widget, EventBus eventBus,
			HistoryManager historyManager, CommonResources commonResources,
			CoreResources coreResources) {
		this.coreWidget = widget;
		this.eventBus = eventBus;
		this.historyManager = historyManager;
		this.commonResources = commonResources;
		this.coreResources = coreResources;
	}

	public void init() {
		RootPanel.get().add(coreWidget);
		commonResources.style().ensureInjected();
		coreResources.style().ensureInjected();

		eventBus.addHandler(AppStateEvent.getType(), new AppStateHandler() {
			@Override
			public void appStateChanged(AppStateEvent event) {
				handleAppState(event.getAppState());
			}
		});

		historyManager.fireCurrentState();
	}

	private void handleAppState(final AppState appState) {
		AsyncCallback<PlacePresenter> callback = new ManagedCallback<PlacePresenter>() {
			@Override
			public void onSuccess(PlacePresenter result) {
				if (currentPresenter != result) {
					assert (currentPresenter == null);
					currentPresenter = result;
					currentPresenter.init();
				}

				IsWidget content = currentPresenter.getDisplay();
				coreWidget.getContentPanel().clear();
				coreWidget.getContentPanel().add(content.asWidget());

				try {
					currentPresenter.bind(appState.getParameters());
				} catch (IllegalArgumentException iax) {
					AppStateException asx = new AppStateException(
							"place presenter rejected AppState", appState);
					asx.initCause(iax);
					historyManager.handle(asx);
				}
			}
		};

		if (appState.getAppPlace().equals(currentPlace)) {
			callback.onSuccess(currentPresenter);
		} else {
			if (currentPresenter != null) {
				currentPresenter.destroy();
				currentPresenter = null;
			}

			/*
			 * The cast and SuppressWarnings is needed until the fix for GIN
			 * issue 110 makes it into Maven central.
			 */
			currentPlace = appState.getAppPlace();
			@SuppressWarnings("unchecked")
			AsyncProvider<PlacePresenter> presenterProvider = (AsyncProvider<PlacePresenter>) currentPlace
					.getPresenter();
			presenterProvider.get(callback);
		}
	}
}
