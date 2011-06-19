/*
 * ArticleTableColumn.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.client.jump;

import static com.google.common.base.Preconditions.*;

import java.util.Collections;
import java.util.List;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Hyperlink;
import com.prealpha.extempdb.client.AppPlace;
import com.prealpha.extempdb.client.AppState;
import com.prealpha.extempdb.shared.dto.ArticleDto;

class ArticleTableColumn extends Column<ArticleDto, String> {
	private final ArticleField field;

	public ArticleTableColumn(ArticleField field) {
		super(field.equals(ArticleField.TITLE) ? new TitleCell()
				: new TextCell());
		checkNotNull(field);
		this.field = field;
	}

	@Override
	public String getValue(ArticleDto article) {
		return field.getField(article);
	}

	private static class TitleCell extends AbstractCell<String> {
		@Override
		public void render(Context context, String value, SafeHtmlBuilder sb) {
			List<String> parameters = Collections.singletonList(context
					.getKey().toString());
			AppState appState = new AppState(AppPlace.ARTICLE, parameters);
			Hyperlink hyperlink = new Hyperlink(value, appState.toString());
			sb.appendHtmlConstant(hyperlink.toString());
		}
	}
}
