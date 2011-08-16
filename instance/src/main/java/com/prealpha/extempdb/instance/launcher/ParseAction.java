/*
 * ParseAction.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.launcher;

import static com.google.common.base.Preconditions.*;

import java.net.URL;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import com.prealpha.extempdb.domain.Article;
import com.prealpha.extempdb.domain.ArticleUrl;
import com.prealpha.extempdb.domain.ArticleUrl_;
import com.prealpha.extempdb.domain.DistributedEntity;
import com.prealpha.extempdb.instance.server.parse.ArticleParseException;
import com.prealpha.extempdb.instance.server.parse.ArticleParser;
import com.prealpha.extempdb.instance.server.parse.ParserNotFoundException;
import com.prealpha.extempdb.util.logging.InjectLogger;

public final class ParseAction implements UserAction {
	@InjectLogger
	private Logger log;

	private final EntityManager entityManager;

	private final ArticleParser articleParser;

	private final Date timestamp;

	private final Set<ArticleUrl> urls;

	@Inject
	private ParseAction(EntityManager entityManager, ArticleParser articleParser) {
		this.entityManager = entityManager;
		this.articleParser = articleParser;
		timestamp = new Date();

		this.entityManager.getTransaction().begin();
		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
		CriteriaQuery<ArticleUrl> criteria = builder
				.createQuery(ArticleUrl.class);
		Root<ArticleUrl> root = criteria.from(ArticleUrl.class);
		criteria.where(builder.isNull(root.get(ArticleUrl_.child)));
		List<ArticleUrl> resultList = this.entityManager.createQuery(criteria)
				.getResultList();
		urls = ImmutableSet.copyOf(resultList);
		this.entityManager.getTransaction().commit();
	}
	
	@Override
	public Type getType() {
		return Type.INFO;
	}

	@Override
	public Date getTimestamp() {
		return new Date(timestamp.getTime());
	}

	@Override
	public URL getUrl() {
		return null;
	}

	@Override
	public String toString() {
		return "Parsing articles...";
	}

	@Override
	public Iterator<Runnable> iterator() {
		return Iterators.transform(urls.iterator(),
				new Function<ArticleUrl, Runnable>() {
					@Override
					public Runnable apply(ArticleUrl input) {
						return new ParseTask(input);
					}
				});
	}

	@Override
	public boolean apply(DistributedEntity entity) {
		if (entity instanceof Article) {
			ArticleUrl url = ((Article) entity).getUrl();
			return urls.contains(url);
		} else {
			return false;
		}
	}

	@Override
	public int size() {
		return urls.size();
	}

	private final class ParseTask implements Runnable {
		private final ArticleUrl url;

		private ParseTask(ArticleUrl url) {
			checkNotNull(url);
			this.url = url;
		}

		@Transactional
		@Override
		public void run() {
			try {
				Article article = articleParser.parse(url);
				entityManager.persist(article);
			} catch (ArticleParseException apx) {
				log.warn("exception occurred while parsing", apx);
			} catch (ParserNotFoundException pnfx) {
				log.error("parser not found", pnfx);
			}
		}
	}
}
