/*
 * AddMapping.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.shared.action;

import static com.google.common.base.Preconditions.*;

import com.prealpha.dispatch.shared.Action;
import com.prealpha.extempdb.shared.dto.ArticleDto;
import com.prealpha.extempdb.shared.dto.TagDto;
import com.prealpha.extempdb.shared.id.UserSessionToken;

public class AddMapping implements Action<MutationResult> {
	private TagDto tag;

	private ArticleDto article;

	private UserSessionToken sessionToken;

	// serialization support
	@SuppressWarnings("unused")
	private AddMapping() {
	}

	public AddMapping(TagDto tag, ArticleDto article,
			UserSessionToken sessionToken) {
		checkNotNull(tag);
		checkNotNull(article);
		checkNotNull(sessionToken);

		this.tag = tag;
		this.article = article;
		this.sessionToken = sessionToken;
	}

	public TagDto getTag() {
		return tag;
	}

	public ArticleDto getArticle() {
		return article;
	}

	public UserSessionToken getSessionToken() {
		return sessionToken;
	}
}