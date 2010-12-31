/*
 * GetMappingsByTag.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.shared.action;

import static com.google.common.base.Preconditions.*;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.prealpha.extempdb.shared.dto.TagDto;
import com.prealpha.extempdb.shared.dto.TagMappingDto;
import com.prealpha.extempdb.shared.dto.TagMappingDto.State;

public class GetMappingsByTag extends GetMappings {
	private static final Set<State> ALL_STATES = ImmutableSet.copyOf(State
			.values());

	private TagDto tag;

	private HashSet<State> states;

	private Comparator<? super TagMappingDto> comparator;

	// serialization support
	@SuppressWarnings("unused")
	private GetMappingsByTag() {
	}

	public GetMappingsByTag(TagDto tag) {
		this(tag, ALL_STATES, null);
	}

	public GetMappingsByTag(TagDto tag, Set<State> states) {
		this(tag, states, null);
	}

	public GetMappingsByTag(TagDto tag,
			Comparator<? super TagMappingDto> comparator) {
		this(tag, ALL_STATES, comparator);
	}

	public GetMappingsByTag(TagDto tag, Set<State> states,
			Comparator<? super TagMappingDto> comparator) {
		checkNotNull(tag);
		checkNotNull(states);
		checkArgument(!states.contains(null));
		this.tag = tag;
		this.states = new HashSet<State>(states);
		this.comparator = comparator;
	}

	public TagDto getTag() {
		return tag;
	}

	public Set<State> getStates() {
		return Collections.unmodifiableSet(states);
	}

	public Comparator<? super TagMappingDto> getComparator() {
		return comparator;
	}

	@Override
	public boolean apply(TagMappingDto mapping) {
		if (!tag.equals(mapping.getTag())) {
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
		result = prime * result + ((tag == null) ? 0 : tag.hashCode());
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
		if (tag == null) {
			if (other.tag != null) {
				return false;
			}
		} else if (!tag.equals(other.tag)) {
			return false;
		}
		return true;
	}
}
