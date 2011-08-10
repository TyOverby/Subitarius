/*
 * PointsPresenter.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.client.main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.prealpha.dispatch.shared.DispatcherAsync;
import com.prealpha.extempdb.instance.client.Presenter;
import com.prealpha.extempdb.instance.client.error.ManagedCallback;
import com.prealpha.extempdb.instance.shared.action.GetPoints;
import com.prealpha.extempdb.instance.shared.action.GetPointsResult;
import com.prealpha.extempdb.instance.shared.dto.UserDto;

public class PointsPresenter implements Presenter<UserDto> {
	public static interface Display extends IsWidget {
		boolean isVisible();

		void setVisible(boolean visible);

		void add(String userName, int points);

		void clear();
	}

	private final Display display;

	private final DispatcherAsync dispatcher;

	@Inject
	public PointsPresenter(Display display, DispatcherAsync dispatcher) {
		this.display = display;
		this.dispatcher = dispatcher;
	}

	@Override
	public Display getDisplay() {
		return display;
	}

	@Override
	public void bind(UserDto user) {
		display.setVisible(user != null);

		if (display.isVisible()) {
			GetPoints action = GetPoints.INSTANCE;
			dispatcher.execute(action, new ManagedCallback<GetPointsResult>() {
				@Override
				public void onSuccess(GetPointsResult result) {
					Map<UserDto, Integer> points = result.getPoints();
					points = sortByValuesDescending(points);
					display.clear();
					for (Map.Entry<UserDto, Integer> entry : points.entrySet()) {
						display.add(entry.getKey().getName(), entry.getValue());
					}
				}
			});
		}
	}

	private static <K, V extends Comparable<? super V>> Map<K, V> sortByValuesDescending(
			Map<? extends K, ? extends V> map) {
		List<Map.Entry<? extends K, ? extends V>> entryList = new ArrayList<Map.Entry<? extends K, ? extends V>>(
				map.entrySet());
		Collections.sort(entryList,
				new Comparator<Map.Entry<? extends K, ? extends V>>() {
					@Override
					public int compare(Map.Entry<? extends K, ? extends V> e1,
							Map.Entry<? extends K, ? extends V> e2) {
						return -1 * e1.getValue().compareTo(e2.getValue());
					}
				});

		Map<K, V> resultMap = new LinkedHashMap<K, V>();
		for (Map.Entry<? extends K, ? extends V> entry : entryList) {
			resultMap.put(entry.getKey(), entry.getValue());
		}
		return resultMap;
	}
}
