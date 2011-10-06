/*
 * HereParseThis.java
 * Copyright (C) 2011 Ty Overby
 * All rights reserved.
 */

package com.subitarius.instance.server.parse;

import com.google.inject.Injector;
import com.subitarius.domain.Article;
import com.subitarius.domain.ArticleUrl;

/**
 * @author ty
 * 
 */
public class HereParseThis {

	/**
	 * @param args
	 * @throws ArticleParseException
	 */
	public static void main(String[] args) throws ArticleParseException {
		String earl = "http://articles.latimes.com/2011/may/03/world/la-fg-canada-election-20110504";

		printArticle(parseThisMotherFucker(earl));
	}

	private static Article parseThisMotherFucker(String earl) throws ArticleParseException {
		TestingContextListener tcl = new TestingContextListener();
		Injector inj = tcl.getInjector();

		ArticleParser ap = inj.getInstance(ArticleParser.class);

		return ap.parse(new ArticleUrl(earl));
	}

	private static void printArticle(Article article) {
		System.out.println("TITLE:    " + article.getTitle());
		System.out.println("EARL: " + article.getUrl());
		System.out.println("AUTHOR: " + article.getByline());
		System.out.println("DATE: " + article.getDate());
		System.out.println("PARAGRAPHS:");
		for (String s : article.getParagraphs()) {
			System.out.println("\t" + s);
		}
	}
}
