/*
 * GetMappingsByTag.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.shared.action;

import static com.google.common.base.Preconditions.*;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.prealpha.extempdb.instance.shared.dto.ArticleDto;
import com.prealpha.extempdb.instance.shared.dto.TagMappingDto;
import com.prealpha.extempdb.instance.shared.dto.TagMappingDto.State;

/*
 * Note that hashCode() and equals() ignore the tag name's case.
 */
public class GetMappingsByTag extends GetMappings {
	private static final Set<State> ALL_STATES = ImmutableSet.copyOf(State
			.values());

	private String tagName;

	private HashSet<State> states;

	private Comparator<? super ArticleDto> comparator;

	// serialization support
	@SuppressWarnings("unused")
	private GetMappingsByTag() {
	}

	public GetMappingsByTag(String tagName) {
		this(tagName, ALL_STATES, null);
	}

	public GetMappingsByTag(String tagName, Set<State> states) {
		this(tagName, states, null);
	}

	public GetMappingsByTag(String tagName,
			Comparator<? super ArticleDto> comparator) {
		this(tagName, ALL_STATES, comparator);
	}

	public GetMappingsByTag(String tagName, Set<State> states,
			Comparator<? super ArticleDto> comparator) {
		checkNotNull(tagName);
		checkNotNull(states);
		checkArgument(!states.contains(null));
		this.tagName = tagName;
		this.states = new HashSet<State>(states);
		this.comparator = comparator;
	}

	public String getTagName() {
		return tagName;
	}

	public Set<State> getStates() {
		return Collections.unmodifiableSet(states);
	}

	public Comparator<? super ArticleDto> getComparator() {
		return comparator;
	}

	@Override
	public boolean apply(TagMappingDto mapping) {
		if (!tagName.equals(mapping.getTag().getName())) {
			return false;
		} else if (!states.contains(mapping.getState())) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((comparator == null) ? 0 : comparator.hashCode());
		result = prime * result + ((states == null) ? 0 : states.hashCode());
		result = prime * result
				+ ((tagName == null) ? 0 : tagName.toUpperCase().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof GetMappingsByTag)) {
			return false;
		}
		GetMappingsByTag other = (GetMappingsByTag) obj;
		if (comparator == null) {
			if (other.comparator != null) {
				return false;
			}
		} else if (!comparator.equals(other.comparator)) {
			return false;
		}
		if (states == null) {
			if (other.states != null) {
				return false;
			}
		} else if (!states.equals(other.states)) {
			return false;
		}
		if (tagName == null) {
			if (other.tagName != null) {
				return false;
			}
		} else if (!tagName.equalsIgnoreCase(other.tagName)) {
			return false;
		}
		return true;
	}
}
