/*
 * BrowseState.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.client.browse;

import static com.google.common.base.Preconditions.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.prealpha.extempdb.shared.dto.TagMappingDto.State;
import com.prealpha.extempdb.shared.id.TagName;

/*
 * TODO: what happens on deserialization failure?
 */
final class BrowseState {
	public static BrowseState getInstance(TagName tagName, Set<State> states,
			ArticleSort sort, int pageStart) {
		states = new HashSet<State>(states);
		return new BrowseState(tagName, states, sort, pageStart);
	}

	public static BrowseState deserialize(List<String> parameters) {
		checkArgument(parameters.size() <= 4);

		// initialize all values to defaults
		TagName tagName = null;
		Set<State> states = ImmutableSet.of(State.PATROLLED, State.UNPATROLLED);
		ArticleSort sort = ArticleSort.DEFAULT_SORT;
		int pageStart = 0;

		// change the defaults if there is an appropriate parameter
		switch (parameters.size()) {
		case 4:
			pageStart = Integer.parseInt(parameters.get(3));
		case 3:
			sort = deserializeSort(parameters.get(2));
		case 2:
			states = deserializeStates(parameters.get(1));
		case 1:
			tagName = new TagName(parameters.get(0));
		}

		return new BrowseState(tagName, states, sort, pageStart);
	}

	private final TagName tagName;

	private final Set<State> states;

	private final ArticleSort sort;

	private final int pageStart;

	private BrowseState(TagName tagName, Set<State> states, ArticleSort sort,
			int pageStart) {
		checkNotNull(states);
		checkNotNull(sort);
		checkArgument(!states.contains(null));
		checkArgument(pageStart >= 0);

		this.tagName = tagName;
		this.states = states;
		this.sort = sort;
		this.pageStart = pageStart;
	}

	public TagName getTagName() {
		return tagName;
	}

	public Set<State> getStates() {
		return Collections.unmodifiableSet(states);
	}

	public ArticleSort getSort() {
		return sort;
	}

	public int getPageStart() {
		return pageStart;
	}

	public List<String> serialize() {
		if (tagName == null) {
			return Collections.emptyList();
		} else {
			return ImmutableList.of(tagName.toString(),
					serializeStates(states), serializeSort(sort),
					Integer.toString(pageStart));
		}
	}

	private static String serializeStates(Set<State> states) {
		String str = "";
		for (State state : states) {
			str += state.name() + ",";
		}
		if (states.size() > 0) {
			str = str.substring(0, str.length() - 1);
		}
		return str;
	}

	private static Set<State> deserializeStates(String str) {
		String[] split = str.split(",");
		Set<State> states = new HashSet<State>();
		for (String stateStr : split) {
			states.add(State.valueOf(stateStr));
		}
		return states;
	}

	private static String serializeSort(ArticleSort sort) {
		if (sort == null) {
			return "";
		} else {
			String str = "";
			str += sort.getField().name() + ",";
			str += sort.isAscending() + ",";
			str += serializeSort(sort.getLastSort());
			return str;
		}
	}

	private static ArticleSort deserializeSort(String str) {
		if (str.isEmpty()) {
			return null;
		} else {
			int fieldEnd = str.indexOf(",");
			String fieldStr = str.substring(0, fieldEnd);
			ArticleField field = ArticleField.valueOf(fieldStr);

			int ascendingStart = fieldEnd + 1;
			int ascendingEnd = str.indexOf(",", ascendingStart);
			String ascendingStr = str.substring(ascendingStart, ascendingEnd);
			boolean ascending = Boolean.parseBoolean(ascendingStr);

			int lastSortStart = ascendingEnd + 1;
			String lastSortStr = str.substring(lastSortStart);
			ArticleSort lastSort = deserializeSort(lastSortStr);

			return new ArticleSort(field, ascending, lastSort);
		}
	}
}
