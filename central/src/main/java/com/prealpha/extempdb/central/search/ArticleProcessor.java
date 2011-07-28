/*
 * ArticleProcessor.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.central.search;

import static com.google.common.base.Preconditions.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import com.prealpha.extempdb.domain.Article;
import com.prealpha.extempdb.domain.Article_;
import com.prealpha.extempdb.domain.Source;
import com.prealpha.extempdb.server.parse.ArticleParseException;
import com.prealpha.extempdb.server.parse.ArticleParser;
import com.prealpha.extempdb.server.parse.ArticleParserFactory;
import com.prealpha.extempdb.server.parse.ProtoArticle;
import com.prealpha.extempdb.util.logging.InjectLogger;

public class ArticleProcessor {
	@InjectLogger
	private Logger log;

	private final EntityManager entityManager;

	private final ArticleParserFactory parserFactory;

	@Inject
	public ArticleProcessor(EntityManager entityManager,
			ArticleParserFactory parserFactory) {
		this.entityManager = entityManager;
		this.parserFactory = parserFactory;
	}

	@Transactional
	public Article process(String url) throws ArticleParseException,
			UnsupportedSiteException, URISyntaxException {
		checkNotNull(url);
		URI uri = new URI(url);
		String domainName = uri.getHost();
		Source source = Source.fromDomainName(domainName);
		if (source != null) {
			return process(url, source);
		} else {
			throw new UnsupportedSiteException(domainName);
		}
	}

	@Transactional
	public Article process(String url, Source source)
			throws ArticleParseException {
		checkNotNull(url);
		checkNotNull(source);

		ArticleParser parser = parserFactory.getParser(source);
		String canonicalUrl = parser.getCanonicalUrl(url);
		Article existing = getExistingArticle(canonicalUrl);

		if (existing == null) {
			ProtoArticle protoArticle = parser.parse(canonicalUrl);
			if (protoArticle != null) {
				Article article = new Article();
				protoArticle.fill(article);
				article.setRetrievalDate(new Date());
				article.setUrl(canonicalUrl);
				article.setSource(source);

				log.debug("result article at URL {} parsed and persisted",
						canonicalUrl);
				entityManager.persist(article);
				entityManager.flush();
				return article;
			} else {
				return null;
			}
		} else {
			log.debug("result article at URL {} was previously parsed",
					canonicalUrl);
			return existing;
		}
	}

	private Article getExistingArticle(String url) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Article> criteria = builder.createQuery(Article.class);
		Root<Article> articleRoot = criteria.from(Article.class);
		criteria.where(builder.equal(articleRoot.get(Article_.url), url));

		try {
			Article existing = entityManager.createQuery(criteria)
					.getSingleResult();
			return existing;
		} catch (NoResultException nrx) {
			return null;
		}
	}
}
