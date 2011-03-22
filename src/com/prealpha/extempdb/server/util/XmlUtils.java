/*
 * XmlUtils.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.server.util;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.filter.Filter;
import org.jdom.output.XMLOutputter;
import org.w3c.dom.Node;

import com.prealpha.extempdb.server.parse.ArticleParseException;

public final class XmlUtils {
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

	public static Filter getOrFilter(final Filter f1, final Filter f2) {
		return new Filter() {
			@Override
			public boolean matches(Object obj) {
				return (f1.matches(obj) || f2.matches(obj));
			}
		};
	}

	public static Map<String, String> getMetaMap(Element headElement)
			throws ArticleParseException {
		Map<String, String> metaMap = new HashMap<String, String>();
		Namespace namespace = headElement.getNamespace();

		for (Object obj : headElement.getChildren("meta", namespace)) {
			if (!(obj instanceof Element)) {
				continue;
			}

			Element metaElement = (Element) obj;
			String metaName = metaElement.getAttributeValue("name");
			String metaContent = metaElement.getAttributeValue("content");
			metaMap.put(metaName, metaContent.trim()); // for line breaks
		}

		return metaMap;
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
		return new XMLOutputter().outputString(element);
	}

	private XmlUtils() {
	}
}
