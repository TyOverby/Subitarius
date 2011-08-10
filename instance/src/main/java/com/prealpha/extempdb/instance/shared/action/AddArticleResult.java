/*
 * AddArticleResult.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.shared.action;

import static com.google.common.base.Preconditions.*;

import com.prealpha.dispatch.shared.Result;

public class AddArticleResult implements Result {
	public static enum Type {
		SUCCESS(true),

		INVALID_URL(false),

		NO_PARSER(false),

		NO_ARTICLE(false),

		PARSE_FAILED(false);

		private boolean success;

		private Type(boolean success) {
			this.success = success;
		}

		public boolean isSuccess() {
			return success;
		}
	}

	private Type type;

	private Long articleId;

	// serialization support
	@SuppressWarnings("unused")
	private AddArticleResult() {
	}

	public AddArticleResult(Type type) {
		checkNotNull(type);
		checkArgument(!type.isSuccess());
		this.type = type;
		articleId = null;
	}

	public AddArticleResult(Type type, Long articleId) {
		checkNotNull(type);
		checkNotNull(articleId);
		checkArgument(type.isSuccess());
		this.type = type;
		this.articleId = articleId;
	}

	public Type getType() {
		return type;
	}

	public Long getArticleId() {
		return articleId;
	}
}
