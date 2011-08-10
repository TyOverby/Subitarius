/*
 * JumpState.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.client.jump;

import static com.google.common.base.Preconditions.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.prealpha.extempdb.instance.shared.dto.TagMappingDto.State;

/*
 * Note that hashCode() and equals() ignore the tag name's case.
 */
final class JumpState {
	public static JumpState getInstance(String tagName, Set<State> states,
			ArticleSort sort, int pageStart) {
		states = new HashSet<State>(states);
		return new JumpState(tagName, states, sort, pageStart);
	}

	/**
	 * Deserializes and returns a {@code JumpState} from a list of
	 * {@code String} parameters. The parameters must be in the format returned
	 * by {@link #serialize()}. While the implementation may attempt to recover
	 * from some invalid inputs, clients should not rely on this behavior.
	 * 
	 * @param parameters
	 *            the serialized form of a {@code JumpState}, as returned by
	 *            {@link #serialize()}
	 * @return a {@code JumpState} equal to the instance previously serialized
	 * @throws IllegalArgumentException
	 *             if the parameters are not in the format returned by
	 *             {@link #serialize()}
	 * @throws NullPointerException
	 *             if the list is {@code null}, if any parameters are
	 *             {@code null}, or if the {@link ArticleSort} parameter is an
	 *             empty string (which represents a {@code null}
	 *             {@code ArticleSort})
	 */
	public static JumpState deserialize(List<String> parameters) {
		checkArgument(parameters.size() <= 4);

		// initialize all values to defaults
		String tagName = null;
		Set<State> states = ImmutableSet.of(State.PATROLLED, State.UNPATROLLED);
		ArticleSort sort = ArticleSort.DEFAULT_SORT;
		int pageStart = 0;

		// change the defaults if there is an appropriate parameter
		switch (parameters.size()) {
		case 4:
			try {
				pageStart = Integer.parseInt(parameters.get(3));
			} catch (NumberFormatException nfx) {
				throw new IllegalArgumentException("invalid page start", nfx);
			}
		case 3:
			sort = deserializeSort(parameters.get(2));
		case 2:
			states = deserializeStates(parameters.get(1));
		case 1:
			tagName = parameters.get(0);
		}

		return new JumpState(tagName, states, sort, pageStart);
	}

	private final String tagName;

	private final Set<State> states;

	private final ArticleSort sort;

	private final int pageStart;

	private JumpState(String tagName, Set<State> states, ArticleSort sort,
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

	public String getTagName() {
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
			return ImmutableList.of(tagName, serializeStates(states),
					serializeSort(sort), Integer.toString(pageStart));
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + pageStart;
		result = prime * result + ((sort == null) ? 0 : sort.hashCode());
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
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof JumpState)) {
			return false;
		}
		JumpState other = (JumpState) obj;
		if (pageStart != other.pageStart) {
			return false;
		}
		if (sort == null) {
			if (other.sort != null) {
				return false;
			}
		} else if (!sort.equals(other.sort)) {
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

	@Override
	public String toString() {
		return "JumpState [tagName=" + tagName + ", states=" + states
				+ ", sort=" + sort + ", pageStart=" + pageStart + "]";
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
		if (str.isEmpty()) {
			return Collections.<State> emptySet();
		}

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
			try {
				int fieldEnd = str.indexOf(",");
				String fieldStr = str.substring(0, fieldEnd);
				ArticleField field = ArticleField.valueOf(fieldStr);

				int ascendingStart = fieldEnd + 1;
				int ascendingEnd = str.indexOf(",", ascendingStart);
				String ascendingStr = str.substring(ascendingStart,
						ascendingEnd);
				boolean ascending = Boolean.parseBoolean(ascendingStr);

				int lastSortStart = ascendingEnd + 1;
				String lastSortStr = str.substring(lastSortStart);
				ArticleSort lastSort = deserializeSort(lastSortStr);

				return new ArticleSort(field, ascending, lastSort);
			} catch (IndexOutOfBoundsException ioobx) {
				throw new IllegalArgumentException(ioobx);
			}
		}
	}
}
