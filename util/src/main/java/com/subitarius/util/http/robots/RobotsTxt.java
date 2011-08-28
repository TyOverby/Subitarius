/*
 * RobotsTxt.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.util.http.robots;

import static com.google.common.base.Preconditions.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.Header;
import org.apache.http.client.methods.HttpUriRequest;

import com.google.common.base.Predicate;
import com.subitarius.util.http.robots.Directive.Type;

public class RobotsTxt implements Predicate<HttpUriRequest> {
	private final Map<Set<Directive>, Section> sections;

	private Set<Directive> agents = new HashSet<Directive>();

	private List<Directive> directives;

	private boolean expectingAgents = true;

	public RobotsTxt(String file) {
		sections = new LinkedHashMap<Set<Directive>, Section>();

		for (String line : file.split("\n")) {
			if (line.contains("#")) {
				line = line.substring(0, line.indexOf("#"));
			}

			int index = line.indexOf(":");
			if (index >= 0) {
				String name = line.substring(0, index).trim();
				String value = line.substring(index + 1).trim();

				Type type;
				if (name.equals("Allow")) {
					type = Type.ALLOW;
				} else if (name.equals("Disallow")) {
					type = Type.DISALLOW;
				} else if (name.equals("User-agent")) {
					type = Type.USER_AGENT;
				} else {
					continue;
				}

				if (type.hasPattern()) {
					add(new PatternDirective(value, type));
				} else {
					add(new StaticDirective(value, type));
				}
			}
		}

		if (directives != null) {
			sections.put(agents, new Section(directives));
		}
		agents = null;
		directives = null;
	}

	private void add(Directive directive) {
		if (expectingAgents) {
			switch (directive.getType()) {
			case USER_AGENT:
				agents.add(directive);
				break;
			default:
				expectingAgents = false;
				directives = new ArrayList<Directive>();
				directives.add(directive);
				break;
			}
		} else {
			switch (directive.getType()) {
			case USER_AGENT:
				expectingAgents = true;
				sections.put(agents, new Section(directives));
				agents = new HashSet<Directive>();
				agents.add(directive);
				directives = null;
				break;
			default:
				directives.add(directive);
				break;
			}
		}
	}

	@Override
	public boolean apply(HttpUriRequest request) {
		Header[] headers = request.getHeaders("User-Agent");
		checkArgument(headers.length == 1);
		String userAgent = headers[0].getValue();

		String path = request.getURI().getPath();

		for (Map.Entry<Set<Directive>, Section> entry : sections.entrySet()) {
			for (Directive directive : entry.getKey()) {
				assert directive.getType().equals(Type.USER_AGENT);

				if (directive.apply(userAgent)) {
					if (!entry.getValue().apply(path)) {
						return false;
					}
				}
			}
		}

		return true;
	}

	@Override
	public String toString() {
		String str = "";
		for (Map.Entry<Set<Directive>, Section> entry : sections.entrySet()) {
			for (Directive directive : entry.getKey()) {
				str += directive.toString();
			}
			str += entry.getValue().toString();
		}
		return str;
	}
}
