/*
 * Source.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

import com.google.inject.Injector;
import com.prealpha.extempdb.server.parse.ArticleParser;

@Entity
public class Source {
	private Long id;

	private String domainName;

	private String displayName;

	private String parserClass;
	
	private ArticleParser parser;

	public Source() {
	}

	@Id
	@GeneratedValue
	@Column(nullable = false)
	public Long getId() {
		return id;
	}

	@SuppressWarnings("unused")
	private void setId(Long id) {
		this.id = id;
	}

	@Column(unique = true, nullable = false)
	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	@Column(nullable = false)
	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	@Column(unique = true, nullable = false)
	public String getParserClass() {
		return parserClass;
	}

	public void setParserClass(String parserClass) {
		this.parserClass = parserClass;
	}
	
	@Transient
	public ArticleParser getParser(Injector injector) throws ClassNotFoundException {
		if (parser == null) {
			String parserClassName = source.getParserClass();
			Class<?> parserClass = Class.forName(parserClassName);
			parser = (ArticleParser) injector.getInstance(parserClass);
		}
		return parser;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((domainName == null) ? 0 : domainName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Source)) {
			return false;
		}
		Source other = (Source) obj;
		if (domainName == null) {
			if (other.domainName != null) {
				return false;
			}
		} else if (!domainName.equals(other.domainName)) {
			return false;
		}
		return true;
	}
}
