/*
 * Searcher.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server.search;

import java.util.Date;
import java.util.List;
import java.util.Iterator;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.prealpha.extempdb.server.InjectLogger;
import com.prealpha.extempdb.server.domain.Article;
import com.prealpha.extempdb.server.domain.Article_;
import com.prealpha.extempdb.server.domain.Source;
import com.prealpha.extempdb.server.domain.Tag;
import com.prealpha.extempdb.server.domain.TagMapping;
import com.prealpha.extempdb.server.http.StatusCodeException;
import com.prealpha.extempdb.server.parse.ArticleParseException;
import com.prealpha.extempdb.server.parse.ArticleParser;
import com.prealpha.extempdb.server.parse.ProtoArticle;
import com.wideplay.warp.persist.Transactional;

public class Searcher implements Runnable {
	@InjectLogger
	private Logger log;
	
	private final EntityManager entityManager;
	
	private final SearchProvider searchProvider;
	
	private final Injector injector;
	
	@Inject
	public Searcher(EntityManager entityManager, SearchProvider searchProvider, Injector injector) {
		this.entityManager = entityManager;
		this.searchProvider = searchProvider;
		this.injector = injector;
	}
	
	@Override
	public void run() {
		log.info("starting search");
		try {
			for (Source source : getAll(Source.class)) {
				try {
					for (Tag tag : getAll(Tag.class)) {
						if (tag.isSearched()) {
							SearchQuery query = new SearchQuery(source, tag);
							execute(query);
						}
					}
				} catch (RuntimeException rx) {
					log.error("unexpected exception was thrown", rx);
				}
			}
			log.info("search complete");
		} catch (SearchUnavailableException sux) {
			log.error("search provider was unavailable", sux);
		}
	}
	
	@Transactional
	void execute(SearchQuery query) throws SearchUnavailableException {
		int resultCount = 0;
		List<String> urls = searchProvider.search(query, 1);
		ArticleParser parser = query.getArticleParser(injector);
		
		if (urls.isEmpty()) {
			log.info("found no results for query {}", query);
		} else {
			String url = parser.getCanonicalUrl(urls.get(0));
			Article existing = getExistingArticle(url);
			
			if (existing == null) {
				try {
					ProtoArticle protoArticle = parser.parse(url);
					if (protoArticle != null) {
						Article article = new Article();
						protoArticle.fill(article);
						article.setRetrievalDate(new Date());
						article.setUrl(url);
						article.setSource(query.getSource());
						
						log.debug("result article at URL {} parsed and persisted", url);
						entityManager.persist(article);
						persistIfNew(query.createTagMapping(article));
						resultCount++;
					}
				} catch (ArticleParseException apx) {
					if (apx.getCause() instanceof StatusCodeException) {
						StatusCodeException scx = (StatusCodeException) apx.getCause();
						int statusCode = scx.getStatusCode();
						log.warn("article parse failed due to HTTP status code {}, URL {}", statusCode, url);
					} else {
						log.warn("article parse failed, URL " + url + ": ", apx);
					}
				}
			} else {
				log.debug("result article at URL {} was previously parsed", url);
				persistIfNew(query.createTagMapping(article));
				resultCount++;
			}
			
			log.info("handled {} result(s) for query {}", resultCount, query);
		}
	}
	
	private Article getExistingArticle(String url) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Article> criteria = builder.createQuery(Article.class);
		Root<Article> articleRoot = criteria.from(Article.class);
		criteria.where(builder.equal(articleRoot.get(Article_.url), url));
		
		try {
			Article existing = entityManager.createQuery(criteria).getSingleResult();
			return existing;
		} catch (NoResultException nrx) {
			return null;
		}
	}
	
	private void persistIfNew(TagMapping mapping) {
		Tag tag = mapping.getTag();
		Article article = mapping.getArticle();
		
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<TagMapping> criteria = TagMapping.getCriteria(tag, article, builder);
		
		try {
			TagMapping existing = entityManager.createQuery(criteria).getSingleResult();
			log.debug("mapping from tag \"{}\" to article ID {} already exists", tag.getName(), article.getId());
		} catch (NoResultException nrx) {
			entityManager.persist(mapping);
			log.debug("mapping from tag \"{}\" to article ID {} created and persisted", tag.getName(), article.getId());
		}
	}
	
	@Transactional
	<T> List<T> getAll(Class<T> entityClass) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<T> criteria = builder.createQuery(entityClass);
		Root<T> root = criteria.from(entityClass);
		criteria.select(root);
		criteria.distinct(true);
		return entityManager.createQuery(criteria).getResultList();
	}
}
