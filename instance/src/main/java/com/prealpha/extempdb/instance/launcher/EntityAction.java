/*
 * EntityAction.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.launcher;

import static com.google.common.base.Preconditions.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import com.prealpha.extempdb.domain.Article;
import com.prealpha.extempdb.domain.DistributedEntity;
import com.prealpha.extempdb.domain.TagMapping;

final class EntityAction implements Action {
	private final DistributedEntity entity;

	EntityAction(DistributedEntity entity) {
		checkNotNull(entity);
		this.entity = entity;
	}

	@Override
	public Date getTimestamp() {
		return entity.getPersistDate();
	}

	@Override
	public URL getUrl() {
		String hash;
		if (entity instanceof Article) {
			hash = entity.getHash();
		} else if (entity instanceof TagMapping) {
			Article article = ((TagMapping) entity).getArticleUrl()
					.getArticle();
			hash = article.getHash();
		} else {
			return null;
		}
		try {
			return new URL("http", "localhost", 8080, "#ARTICLE;" + hash);
		} catch (MalformedURLException mux) {
			throw new AssertionError(mux);
		}
	}
	
	@Override
	public String toString() {
		return "new entity: " + entity;
	}
}
