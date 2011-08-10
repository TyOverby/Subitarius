/*
 * ArticleTableHeader.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.client.jump;

import static com.google.common.base.Preconditions.*;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.ClickableTextCell;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.inject.Inject;
import com.prealpha.extempdb.instance.client.CommonResources;

public class ArticleTableHeader extends Header<String> {
	private final CommonResources resources;

	private ArticleField field;

	private ArticleSort sort;

	@Inject
	public ArticleTableHeader(CommonResources resources) {
		super(new ClickableTextCell());
		this.resources = resources;
	}

	public void init(ArticleField field) {
		checkNotNull(field);
		checkState(this.field == null);
		this.field = field;
	}

	public void bind(ArticleSort sort) {
		checkNotNull(sort);
		checkState(field != null);
		this.sort = sort;
	}

	public ArticleField getField() {
		return field;
	}

	@Override
	public String getValue() {
		checkState(field != null);
		return field.toString();
	}

	@Override
	public void render(Context context, SafeHtmlBuilder sb) {
		checkState(field != null);
		checkState(sort != null);

		ImageResource image = (sort.isAscending() ? resources.upArrow()
				: resources.downArrow());

		String html = "<div style=\"position:relative;cursor:pointer;padding-right:"
				+ image.getWidth() + "px;\">";

		if (field.equals(sort.getField())) {
			html += makeImage(image);
		} else {
			html += "<div style=\"position:absolute;display:none;\"></div>";
		}

		html += "<div>" + SafeHtmlUtils.htmlEscape(field.toString()) + "</div>";
		html += "</div>";

		sb.appendHtmlConstant(html);
	}

	private static String makeImage(ImageResource resource) {
		AbstractImagePrototype proto = AbstractImagePrototype.create(resource);
		return proto.getHTML().replace("style='",
				"style='position:absolute;right:0px;top:0px;");
	}
}
