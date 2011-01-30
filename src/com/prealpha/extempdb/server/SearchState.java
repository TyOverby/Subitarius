/*
 * SearchState.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server;

import static com.google.common.base.Preconditions.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.prealpha.extempdb.server.domain.Article;
import com.prealpha.extempdb.server.domain.Article_;
import com.prealpha.extempdb.server.domain.Source;
import com.prealpha.extempdb.server.domain.Tag;
import com.prealpha.extempdb.server.domain.TagMapping;
import com.prealpha.extempdb.server.http.StatusCodeException;
import com.prealpha.extempdb.server.parse.ArticleParseException;
import com.prealpha.extempdb.server.parse.ProtoArticle;
import com.prealpha.extempdb.server.parse.SourceParser;

final class SearchState {
	@InjectLogger
	private Logger log;

	private final Injector injector;

	private final EntityManager entityManager;

	private Tag tag;

	private Source source;

	private boolean initialized;

	private SourceParser parser;

	private Set<String> mappedUrls;

	private int parseCount;

	@Inject
	public SearchState(Injector injector, EntityManager entityManager) {
		this.injector = injector;
		this.entityManager = entityManager;
	}

	public boolean isInitialized() {
		return initialized;
	}

	public void init(Tag tag, Source source) {
		checkState(!initialized);
		checkNotNull(tag);
		checkNotNull(source);

		this.tag = tag;
		this.source = source;
		initialized = true;

		Class<? extends SourceParser> parserClass = source.getParserClass();
		parser = injector.getInstance(parserClass);
		mappedUrls = new HashSet<String>();
	}

	public int getMappingCount() {
		checkState(initialized);
		return mappedUrls.size();
	}

	public int getParseCount() {
		checkState(initialized);
		return parseCount;
	}

	public boolean shouldContinue(int count) {
		checkState(initialized);
		return (mappedUrls.size() < Math.log(count));
	}

	public void handle(String rawUrl) {
		checkState(initialized);
		String url = parser.getCanonicalUrl(rawUrl);
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();

		CriteriaQuery<Article> articleCriteria = builder
				.createQuery(Article.class);
		Root<Article> articleRoot = articleCriteria.from(Article.class);
		articleCriteria
				.where(builder.equal(articleRoot.get(Article_.url), url));
		Article existing = entityManager.createQuery(articleCriteria)
				.getSingleResult();

		if (existing != null) {
			CriteriaQuery<TagMapping> mappingCriteria = TagMapping.getCriteria(
					tag, existing, builder);
			TagMapping mapping = entityManager.createQuery(mappingCriteria)
					.getSingleResult();

			if (mapping == null) {
				mapArticle(existing);
			}

			mappedUrls.add(url);
		} else {
			try {
				ProtoArticle protoArticle = parser.parse(url);

				if (protoArticle != null) {
					Article article = new Article();
					protoArticle.fill(article);

					article.setRetrievalDate(new Date());
					article.setUrl(url);
					article.setSource(source);

					entityManager.persist(article);
					parseCount++;

					mapArticle(article);
					mappedUrls.add(url);
				}
			} catch (ArticleParseException apx) {
				if (apx.getCause() instanceof StatusCodeException) {
					StatusCodeException scx = (StatusCodeException) apx
							.getCause();
					int statusCode = scx.getStatusCode();
					log.warn(
							"article parse failed due to HTTP status code {}, URL {}",
							statusCode, url);
				} else {
					log.warn("article parse failed, URL " + url + ": ", apx);
				}
			}
		}
	}

	private void mapArticle(Article article) {
		checkState(initialized);
		checkNotNull(article);

		TagMapping mapping = new TagMapping();
		mapping.setTag(tag);
		mapping.setArticle(article);
		mapping.setAdded(new Date());
		entityManager.persist(mapping);
	}
}
