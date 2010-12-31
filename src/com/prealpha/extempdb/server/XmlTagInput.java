/*
 * XmlTagInput.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.prealpha.extempdb.server.domain.Tag;
import com.prealpha.extempdb.server.persistence.PersistenceModule;
import com.prealpha.extempdb.server.persistence.TagDao;
import com.prealpha.extempdb.server.persistence.Transactional;

class XmlTagInput {
	public static void main(String[] args) throws IOException, JDOMException {
		Injector injector = Guice.createInjector(new PersistenceModule());
		XmlTagInput tagInput = injector.getInstance(XmlTagInput.class);
		InputStream stream = new FileInputStream(args[0]);
		tagInput.input(stream);
	}

	private final SAXBuilder builder;

	private final TagDao tagDao;

	private final Map<String, Tag> tags;

	@Inject
	public XmlTagInput(SAXBuilder builder, TagDao tagDao) {
		this.tagDao = tagDao;
		this.builder = builder;
		tags = new HashMap<String, Tag>();
	}

	@Transactional
	public void input(InputStream stream) throws IOException, JDOMException {
		Document document = builder.build(stream);
		Tag tag = parseElement(document.getRootElement());
		tagDao.save(tag);
	}

	private Tag parseElement(Element element) {
		if (!element.getName().equals("tag")) {
			throw new IllegalArgumentException();
		}

		String name = element.getAttributeValue("name");
		String searchedAttr = element.getAttributeValue("searched");
		boolean searched = (searchedAttr == null || Boolean
				.parseBoolean(searchedAttr));

		Set<Tag> children = new HashSet<Tag>();
		for (Object obj : element.getChildren("tag")) {
			Element child = (Element) obj;
			children.add(parseElement(child));
		}

		Tag tag;
		if (tags.containsKey(name)) {
			tag = tags.get(name);
			tag.getChildren().addAll(children);
		} else {
			tag = new Tag();
			tag.setName(name);
			tag.setSearched(searched);
			tag.setChildren(children);

			tagDao.save(tag);
			tags.put(name, tag);
		}

		for (Tag child : children) {
			Set<Tag> parents = child.getParents();
			if (parents == null) {
				parents = new HashSet<Tag>();
				child.setParents(parents);
			}
			child.getParents().add(tag);
		}

		return tag;
	}
}
