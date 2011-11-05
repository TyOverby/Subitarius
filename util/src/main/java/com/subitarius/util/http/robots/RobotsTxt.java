/*
 * RobotsTxt.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.util.http.robots;

import java.util.Set;

import org.apache.http.client.methods.HttpUriRequest;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public final class RobotsTxt implements Predicate<HttpUriRequest> {
	private final ImmutableSet<Record> records;

	public RobotsTxt() {
		records = ImmutableSet.of();
	}

	public RobotsTxt(String text) {
		Set<Record> records = Sets.newHashSet();
		String[] lines = text.split("\n");
		String recordText = "";
		for (String line : lines) {
			if (line.isEmpty()) {
				records.add(new Record(recordText));
				recordText = "";
			} else {
				recordText += line;
			}
		}
		if (!recordText.isEmpty()) {
			records.add(new Record(recordText));
		}
		this.records = ImmutableSet.copyOf(records);
	}

	/**
	 * Returns {@code false} if {@code input} is blocked by this
	 * {@code robots.txt} file. A request is blocked if one or more individual
	 * {@link Record} instances which form this file block the request.
	 * 
	 * @param input
	 *            the request to consider for blocking
	 * @return {@code true} if the request is allowed, or {@code false} if it is
	 *         disallowed
	 */
	@Override
	public boolean apply(HttpUriRequest input) {
		for (Record record : records) {
			if (!record.apply(input)) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public String toString() {
		String result = "";
		for (Record record : records) {
			result += record.toString();
		}
		return result;
	}
}
