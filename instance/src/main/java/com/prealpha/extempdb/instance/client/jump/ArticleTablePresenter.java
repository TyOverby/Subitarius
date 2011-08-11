/*
 * ArticleTablePresenter.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.client.jump;

import static com.google.common.base.Preconditions.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import com.google.common.collect.ImmutableList;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import com.google.gwt.view.client.RangeChangeEvent;
import com.google.inject.Inject;
import com.prealpha.dispatch.shared.DispatcherAsync;
import com.prealpha.extempdb.instance.client.Presenter;
import com.prealpha.extempdb.instance.client.error.ManagedCallback;
import com.prealpha.extempdb.instance.shared.action.GetArticleByUrl;
import com.prealpha.extempdb.instance.shared.action.GetArticleResult;
import com.prealpha.extempdb.instance.shared.dto.ArticleDto;
import com.prealpha.extempdb.instance.shared.dto.ArticleUrlDto;
import com.prealpha.extempdb.instance.shared.dto.TagMappingDto;

public class ArticleTablePresenter implements Presenter<List<TagMappingDto>> {
	public static interface Display extends IsWidget, HasValue<ArticleSort> {
		HasData<ArticleDto> getDataDisplay();

		boolean isVisible();

		void setVisible(boolean visible);
	}

	private final Display display;

	private final DispatcherAsync dispatcher;

	private List<TagMappingDto> mappings = ImmutableList.of();

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
	public void bind(List<TagMappingDto> mappings) {
		this.mappings = ImmutableList.copyOf(mappings);
		display.setVisible(mappings.size() > 0);
		display.getDataDisplay().setRowCount(mappings.size(), true);
		updateData();
	}

	private void updateData() {
		Range range = display.getDataDisplay().getVisibleRange();
		int start = range.getStart();
		int length = range.getLength();
		List<TagMappingDto> subList;

		if (start >= mappings.size()) {
			subList = Collections.emptyList();
		} else if (start + length > mappings.size()) {
			subList = mappings.subList(start, mappings.size());
		} else {
			subList = mappings.subList(start, start + length);
		}

		final PendingState pendingState = new PendingState(start,
				subList.size());

		for (TagMappingDto mapping : subList) {
			final int index = subList.indexOf(mapping);
			ArticleUrlDto articleUrl = mapping.getArticleUrl();
			GetArticleByUrl action = new GetArticleByUrl(articleUrl.getHash());
			dispatcher.execute(action, new ManagedCallback<GetArticleResult>() {
				@Override
				public void onSuccess(GetArticleResult result) {
					ArticleDto article = result.getArticle();
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
