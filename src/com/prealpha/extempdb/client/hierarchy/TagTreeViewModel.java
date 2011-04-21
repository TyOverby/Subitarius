/*
 * TagTreeViewModel.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.client.hierarchy;

import java.util.Collections;
import java.util.List;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.view.client.TreeViewModel;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.prealpha.extempdb.client.AppPlace;
import com.prealpha.extempdb.client.AppState;
import com.prealpha.extempdb.shared.dto.TagDto;

public class TagTreeViewModel implements TreeViewModel {
	private final Provider<ChildTagDataProvider> dataSourceProvider;

	@Inject
	public TagTreeViewModel(Provider<ChildTagDataProvider> dataSourceProvider) {
		this.dataSourceProvider = dataSourceProvider;
	}

	@Override
	public <T> NodeInfo<?> getNodeInfo(T value) {
		ChildTagDataProvider dataSource = dataSourceProvider.get();
		dataSource.init((TagDto) value);
		return new DefaultNodeInfo<TagDto>(dataSource, new TagCell());
	}

	@Override
	public boolean isLeaf(Object value) {
		TagDto tag = (TagDto) value;
		return ChildTagDataProvider.isKnownLeaf(tag.getName());
	}

	private static final class TagCell extends AbstractCell<TagDto> {
		@Override
		public void render(Context context, TagDto value, SafeHtmlBuilder sb) {
			String name = value.getName();

			if (value.isSearched()) {
				List<String> parameters = Collections.singletonList(name);
				AppState appState = new AppState(AppPlace.BROWSE, parameters);
				Hyperlink hyperlink = new Hyperlink(name, appState.toString());
				sb.appendHtmlConstant(hyperlink.toString());
			} else {
				sb.appendEscaped(name);
			}
		}
	}
}
