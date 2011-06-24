/*
 * ParseUtils.java
 * Copyright (C) 2011 Meyer Kizner, Ty Overby
 * All rights reserved.
 */

package com.prealpha.extempdb.server.parse;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Parent;
import org.jdom.Text;
import org.jdom.filter.Filter;
import org.jdom.input.DOMBuilder;
import org.jdom.output.XMLOutputter;
import org.w3c.dom.Node;
import org.w3c.tidy.Tidy;

import com.google.common.collect.Iterators;
import com.google.inject.Inject;

final class ParseUtils {
	@Inject
	private static Tidy tidy;

	@Inject
	private static DOMBuilder builder;

	@Inject
	private static XMLOutputter xmlOutputter;

	public static Document parse(InputStream stream) {
		org.w3c.dom.Document dom = tidy.parseDOM(stream, null);
		dom.removeChild(dom.getDoctype());
		Document document = builder.build(dom);
		return document;
	}

	public static List<Element> searchDescendants(Parent content,
			String elementName) {
		return searchDescendants(content, elementName, null, null);
	}

	public static List<Element> searchDescendants(Parent content,
			String elementName, String attribute, String attributeValue) {
		Filter filter = ParseUtils.getElementFilter(elementName, attribute,
				attributeValue);
		@SuppressWarnings("unchecked")
		Iterator<Element> iterator = content.getDescendants(filter);
		List<Element> elements = new ArrayList<Element>();
		Iterators.addAll(elements, iterator);
		return elements;
	}

	public static Filter getElementFilter(final String elementName,
			final String attribute, final String attributeValue) {
		return new Filter() {
			@Override
			public boolean matches(Object obj) {
				if (!(obj instanceof Element)) {
					return false;
				} else {
					Element element = (Element) obj;

					if (!element.getName().equals(elementName)) {
						return false;
					} else if (attribute != null) {
						String actualAttrValue = element
								.getAttributeValue(attribute);

						if (!attributeValue.equals(actualAttrValue)) {
							return false;
						}
					}

					return true;
				}
			}
		};
	}

	public static Filter getOrFilter(final Filter... filters) {
		return new Filter() {
			@Override
			public boolean matches(Object obj) {
				for (Filter filter : filters) {
					if (filter.matches(obj)) {
						return true;
					}
				}
				return false;
			}
		};
	}

	public static Map<String, String> getMetaMap(Element headElement)
			throws ArticleParseException {
		Map<String, String> metaMap = new HashMap<String, String>();

		for (Element metaElement : searchDescendants(headElement, "meta")) {
			String metaName = metaElement.getAttributeValue("name");
			String metaContent = metaElement.getAttributeValue("content");
			metaMap.put(metaName, metaContent.trim()); // for line breaks
		}

		return metaMap;
	}

	public static List<String> getFullText(Parent content) {
		List<String> fullText = new ArrayList<String>();

		for (Object obj : content.getContent()) {
			if (obj instanceof Parent) {
				fullText.addAll(getFullText(content));
			} else if (obj instanceof Text) {
				String text = (((Text) obj).getValue()).trim();

				if (!text.isEmpty() && !(text == null)) {
					fullText.add(text);
				}
			}
		}

		return fullText;
	}

	public static String xmlToString(Node node) {
		try {
			Source source = new DOMSource(node);
			StringWriter stringWriter = new StringWriter();
			Result result = new StreamResult(stringWriter);
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();
			transformer.transform(source, result);
			return stringWriter.getBuffer().toString();
		} catch (TransformerException tx) {
			tx.printStackTrace();
			return null;
		}
	}

	public static String xmlToString(Element element) {
		return xmlOutputter.outputString(element);
	}

	private ParseUtils() {
	}
}
