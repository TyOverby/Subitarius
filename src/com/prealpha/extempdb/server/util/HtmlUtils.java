/*
 * HtmlUtils.java
 * Copyright (C) 2011 Ty Overby, Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Text;
import org.jdom.filter.Filter;
import org.jdom.input.DOMBuilder;
import org.w3c.tidy.Tidy;

import com.google.common.collect.Iterators;

public class HtmlUtils {
	private static final Tidy tidy = new Tidy();
	
	private static final DOMBuilder builder = new DOMBuilder();

	public static Document parse(String page) {
		tidy.setShowWarnings(false);
		tidy.setHideComments(true);
		tidy.setShowErrors(0);
		tidy.setQuiet(true);
		tidy.setInputEncoding("UTF-8");

		InputStream in = new ByteArrayInputStream(page.getBytes());
		org.w3c.dom.Document dom = tidy.parseDOM(in, null);
		dom.removeChild(dom.getDoctype());
		Document document = builder.build(dom);
		return document;
	}

	public static Document parse(URL url) throws IOException {
		tidy.setShowWarnings(false);
		tidy.setHideComments(true);
		tidy.setShowErrors(0);
		tidy.setQuiet(true);
		tidy.setInputEncoding("UTF-8");

		org.w3c.dom.Document dom = tidy.parseDOM(url.openStream(), null);
		dom.removeChild(dom.getDoctype());
		// tidy.pprint(dom, System.out);
		Document document = builder.build(dom);
		return document;
	}

	public static Document parse(InputStream stream) {
		tidy.setShowWarnings(false);
		tidy.setHideComments(true);
		tidy.setShowErrors(0);
		tidy.setQuiet(true);
		tidy.setInputEncoding("UTF-8");

		org.w3c.dom.Document dom = tidy.parseDOM(stream, null);
		dom.removeChild(dom.getDoctype());
		Document document = builder.build(dom);
		return document;
	}

	public static List<Element> getMatches(Element element, String type,
			String key, String value) {
		Filter commentFilter = XmlUtils.getElementFilter(type, key, value);
		Iterator<Element> comments = element.getDescendants(commentFilter);

		List<Element> list = new ArrayList<Element>();
		Iterators.addAll(list, comments);

		return list;
	}

	public static List<Element> getMatches(Document document, String type,
			String key, String value) {
		return getMatches(document.getRootElement(), type, key, value);
	}

	public static List<String> getFullText(Element element) {
		List<String> fullText = new ArrayList<String>();

		for (Object o : element.getContent()) {
			if (o instanceof Element) {
				String toAdd = ((Element) o).getValue().trim();

				if (!toAdd.isEmpty() && !(toAdd == null)) {
					fullText.add(toAdd);
				}
			}
			if (o instanceof Text) {
				String toAdd = (((Text) o).getValue()).trim();

				if (!toAdd.isEmpty() && !(toAdd == null)) {
					fullText.add(toAdd);
				}
			}
		}
		return fullText;
	}
}
