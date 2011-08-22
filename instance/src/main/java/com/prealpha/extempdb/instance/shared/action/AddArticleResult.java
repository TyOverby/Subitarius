/*
 * AddArticleResult.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.shared.action;

import static com.google.common.base.Preconditions.*;

import com.prealpha.dispatch.shared.Result;

public final class AddArticleResult implements Result {
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

	private String articleHash;

	// serialization support
	@SuppressWarnings("unused")
	private AddArticleResult() {
	}

	public AddArticleResult(Type type) {
		checkNotNull(type);
		checkArgument(!type.isSuccess());
		this.type = type;
		articleHash = null;
	}

	public AddArticleResult(Type type, String articleHash) {
		checkNotNull(type);
		checkNotNull(articleHash);
		checkArgument(type.isSuccess());
		this.type = type;
		this.articleHash = articleHash;
	}

	public Type getType() {
		return type;
	}

	public String getArticleHash() {
		return articleHash;
	}
}
