/*
 * TagTreeViewModel.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.client.hierarchy;

import java.util.Collections;
import java.util.List;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.TreeViewModel;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.prealpha.extempdb.client.AppPlace;
import com.prealpha.extempdb.client.AppState;
import com.prealpha.extempdb.shared.dto.TagDto;

public class TagTreeViewModel implements TreeViewModel {
	private static final ProvidesKey<TagDto> PROVIDES_KEY = new ProvidesKey<TagDto>() {
		@Override
		public Object getKey(TagDto tag) {
			return (tag == null ? null : tag.getName());
		}
	};

	class TagNodeInfo implements NodeInfo<TagDto> {
		private final TagDto parent;

		private ChildTagDataProvider dataSource;

		private HasData<TagDto> display;

		public TagNodeInfo(TagDto parent) {
			this.parent = parent;
		}

		@Override
		public Cell<TagDto> getCell() {
			return new AbstractCell<TagDto>() {
				@Override
				public void render(Context context, TagDto value,
						SafeHtmlBuilder sb) {
					String name = value.getName();

					if (value.isSearched()) {
						List<String> parameters = Collections
								.singletonList(name);
						AppState appState = new AppState(AppPlace.BROWSE,
								parameters);
						Hyperlink hyperlink = new Hyperlink(name,
								appState.toString());
						sb.appendHtmlConstant(hyperlink.toString());
					} else {
						sb.appendEscaped(name);
					}
				}
			};
		}

		@Override
		public ProvidesKey<TagDto> getProvidesKey() {
			return PROVIDES_KEY;
		}

		@Override
		public SelectionModel<? super TagDto> getSelectionModel() {
			return selectionModel;
		}

		@Override
		public ValueUpdater<TagDto> getValueUpdater() {
			return null;
		}

		@Override
		public void setDataDisplay(HasData<TagDto> display) {
			this.display = display;
			dataSource = dataSourceProvider.get();
			dataSource.init(parent);
			dataSource.addDataDisplay(display);
		}

		@Override
		public void unsetDataDisplay() {
			dataSource.removeDataDisplay(display);
			dataSource = null;
			display = null;
		}
	}

	private final Provider<ChildTagDataProvider> dataSourceProvider;

	private final SelectionModel<? super TagDto> selectionModel;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Inject
	public TagTreeViewModel(Provider<ChildTagDataProvider> dataSourceProvider,
			SelectionModel selectionModel) {
		this.dataSourceProvider = dataSourceProvider;
		this.selectionModel = selectionModel;
	}

	@Override
	public <T> NodeInfo<?> getNodeInfo(T value) {
		return new TagNodeInfo((TagDto) value);
	}

	@Override
	public boolean isLeaf(Object value) {
		TagDto tag = (TagDto) value;
		return ChildTagDataProvider.isKnownLeaf(tag.getName());
	}
}
