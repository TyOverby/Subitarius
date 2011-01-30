/*
 * ArticleTablePresenter.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.client.browse;

import static com.google.common.base.Preconditions.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import com.google.gwt.view.client.RangeChangeEvent;
import com.google.inject.Inject;
import com.prealpha.dispatch.shared.DispatcherAsync;
import com.prealpha.extempdb.client.Presenter;
import com.prealpha.extempdb.client.error.ManagedCallback;
import com.prealpha.extempdb.shared.action.GetMapping;
import com.prealpha.extempdb.shared.action.GetMappingResult;
import com.prealpha.extempdb.shared.dto.ArticleDto;

public class ArticleTablePresenter implements Presenter<List<Long>> {
	public static interface Display extends IsWidget, HasValue<ArticleSort> {
		HasData<ArticleDto> getDataDisplay();

		boolean isVisible();

		void setVisible(boolean visible);
	}

	private final Display display;

	private final DispatcherAsync dispatcher;

	private List<Long> mappingIds = Collections.emptyList();

	@Inject
	public ArticleTablePresenter(Display display, DispatcherAsync dispatcher) {
		this.display = display;
		this.dispatcher = dispatcher;

		display.getDataDisplay().addRangeChangeHandler(
				new RangeChangeEvent.Handler() {
					@Override
					public void onRangeChange(RangeChangeEvent event) {
						updateData();
					}
				});
	}

	@Override
	public Display getDisplay() {
		return display;
	}

	@Override
	public void bind(List<Long> mappingIds) {
		this.mappingIds = mappingIds;
		display.setVisible(mappingIds.size() > 0);
		display.getDataDisplay().setRowCount(mappingIds.size(), true);
		updateData();
	}

	private void updateData() {
		Range range = display.getDataDisplay().getVisibleRange();
		int start = range.getStart();
		int length = range.getLength();
		List<Long> subList;

		if (start >= mappingIds.size()) {
			subList = Collections.emptyList();
		} else if (start + length > mappingIds.size()) {
			subList = mappingIds.subList(start, mappingIds.size());
		} else {
			subList = mappingIds.subList(start, start + length);
		}

		final PendingState pendingState = new PendingState(start,
				subList.size());

		for (Long mappingId : subList) {
			final int index = subList.indexOf(mappingId);
			GetMapping action = new GetMapping(mappingId);
			dispatcher.execute(action, new ManagedCallback<GetMappingResult>() {
				@Override
				public void onSuccess(GetMappingResult result) {
					ArticleDto article = result.getMapping().getArticle();
					pendingState.update(article, index);
				}
			});
		}
	}

	private class PendingState {
		private final SortedMap<Integer, ArticleDto> articles;

		private final int start;

		private int count;

		public PendingState(int start, int length) {
			articles = new TreeMap<Integer, ArticleDto>();
			this.start = start;
			count = length;
		}

		public void update(ArticleDto article, int index) {
			checkArgument(!articles.containsKey(index));
			articles.put(index, article);

			if (--count <= 0) {
				List<ArticleDto> finalList = new ArrayList<ArticleDto>(
						articles.values());
				display.getDataDisplay().setRowData(start, finalList);
			}
		}
	}
}
