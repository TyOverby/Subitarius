/*
 * ArticleTableWidget.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.client.jump;

import static com.google.common.base.Preconditions.*;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.view.client.HasData;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.prealpha.extempdb.instance.shared.dto.ArticleDto;

public class ArticleTableWidget extends Composite implements
		ArticleTablePresenter.Display {
	private static final int PAGE_SIZE = 12;

	private final HasData<ArticleDto> dataDisplay;

	private final Set<ArticleTableHeader> headers;

	private ArticleSort sort;

	@Inject
	public ArticleTableWidget(CellTable<ArticleDto> cellTable,
			Provider<ArticleTableHeader> headerProvider) {
		dataDisplay = cellTable;
		headers = new HashSet<ArticleTableHeader>();
		sort = ArticleSort.DEFAULT_SORT;

		for (ArticleField field : ArticleField.values()) {
			ArticleTableHeader header = headerProvider.get();
			header.init(field);
			header.bind(sort);
			header.setUpdater(new HeaderUpdater(field));
			headers.add(header);

			Column<ArticleDto, String> column = new ArticleTableColumn(field);
			cellTable.addColumn(column, header);
		}

		cellTable.setPageSize(PAGE_SIZE);

		setValue(ArticleSort.DEFAULT_SORT);
		initWidget(cellTable);
	}

	@Override
	public HasData<ArticleDto> getDataDisplay() {
		return dataDisplay;
	}

	@Override
	public ArticleSort getValue() {
		return sort;
	}

	@Override
	public void setValue(ArticleSort sort) {
		setValue(sort, false);
	}

	@Override
	public void setValue(ArticleSort sort, boolean fireEvents) {
		ArticleSort oldSort = this.sort;
		this.sort = sort;

		for (ArticleTableHeader header : headers) {
			header.bind(sort);
		}

		if (fireEvents) {
			ValueChangeEvent.fireIfNotEqual(this, oldSort, sort);
		}
	}

	@Override
	public HandlerRegistration addValueChangeHandler(
			ValueChangeHandler<ArticleSort> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

	private class HeaderUpdater implements ValueUpdater<String> {
		private final ArticleField field;

		public HeaderUpdater(ArticleField field) {
			checkNotNull(field);
			this.field = field;
		}

		@Override
		public void update(String value) {
			boolean ascending;

			if (field.equals(sort.getField())) {
				ascending = !sort.isAscending();
			} else {
				ascending = true;
			}

			ArticleSort newSort = new ArticleSort(field, ascending, sort);
			setValue(newSort, true);
		}
	}
}
