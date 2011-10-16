/*
 * Record.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.util.http.robots;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.client.methods.HttpUriRequest;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

final class Record implements Predicate<HttpUriRequest> {
	private final String userAgent;

	private final ImmutableSet<String> disallowPrefixes;

	Record(String text) {
		String[] lines = text.split("\n");
		List<String> disallowPrefixes = Lists.newArrayList();
		String userAgent = null;
		for (String line : lines) {
			if (line.indexOf('#') >= 0) {
				line = line.substring(0, line.indexOf('#'));
			}
			if (userAgent == null && line.startsWith("User-agent: ")) {
				userAgent = line.substring(12);
			} else if (line.startsWith("Disallow: ")) {
				// a blank disallow won't affect anything anyway
				if (!line.trim().equals("Disallow:")) {
					disallowPrefixes.add(line.substring(10));
				}
			}
		}
		this.userAgent = userAgent;
		this.disallowPrefixes = ImmutableSet.copyOf(disallowPrefixes);
	}

	/**
	 * Returns {@code false} if {@code input} is blocked by this
	 * {@code robots.txt} record. A request is always allowed if none of its
	 * user agent headers match the declared user agent for the record.
	 * Otherwise, a request is blocked only if one or more of the declared
	 * disallowed prefixes for the record matches the request path.
	 * 
	 * @param input
	 *            the request to consider for blocking
	 * @return {@code true} if the request is allowed, or {@code false} if it is
	 *         disallowed
	 */
	@Override
	public boolean apply(HttpUriRequest input) {
		if (userAgent == null) {
			return true;
		}

		Header[] agentHeaders = input.getHeaders("User-Agent");
		Collection<String> agents = Collections2.transform(
				Arrays.asList(agentHeaders), new Function<Header, String>() {
					@Override
					public String apply(Header input) {
						return input.getValue();
					}
				});
		boolean agentMatches = userAgent.equals("*");
		for (String agent : agents) {
			if (agent.contains(userAgent)) {
				agentMatches = true;
			}
		}

		String path = input.getURI().getPath();
		boolean disallowMatches = false;
		for (String prefix : disallowPrefixes) {
			if (path.startsWith(prefix)) {
				disallowMatches = true;
			}
		}

		return !(agentMatches && disallowMatches);
	}

	@Override
	public String toString() {
		if (userAgent == null) {
			return "";
		} else {
			String result = "User-agent: " + userAgent + '\n';
			for (String prefix : disallowPrefixes) {
				result += "Disallow: " + prefix + '\n';
			}
			return result + '\n';
		}
	}
}
