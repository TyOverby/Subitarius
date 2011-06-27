/*
 * AppState.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.client;

import static com.google.common.base.Preconditions.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class AppState {
	private final AppPlace appPlace;

	private final List<String> parameters;

	public AppState(String token) throws AppStateException {
		if (token == null || !isValidToken(token)) {
			throw new AppStateException("invalid token \"" + token + "\"");
		}

		String[] split = token.split(";");
		appPlace = AppPlace.valueOf(split[0]);
		parameters = new ArrayList<String>(split.length - 1);
		for (int i = 1; i < split.length; i++) {
			parameters.add(split[i]);
		}
	}

	public AppState(AppPlace place) {
		this(place, Collections.<String> emptyList());
	}

	public AppState(AppPlace place, List<String> parameters) {
		checkNotNull(place);
		checkNotNull(parameters);

		this.appPlace = place;
		this.parameters = new ArrayList<String>(parameters);

		for (String parameter : this.parameters) {
			checkArgument(!parameter.contains(";"));
		}
	}

	public AppPlace getAppPlace() {
		return appPlace;
	}

	public List<String> getParameters() {
		return Collections.unmodifiableList(parameters);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((appPlace == null) ? 0 : appPlace.hashCode());
		result = prime * result
				+ ((parameters == null) ? 0 : parameters.hashCode());
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
		if (!(obj instanceof AppState)) {
			return false;
		}
		AppState other = (AppState) obj;
		if (appPlace != other.appPlace) {
			return false;
		}
		if (parameters == null) {
			if (other.parameters != null) {
				return false;
			}
		} else if (!parameters.equals(other.parameters)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		String token = appPlace.name();
		for (String parameter : parameters) {
			token += ';' + parameter;
		}
		return token;
	}

	private static boolean isValidToken(String token) {
		for (AppPlace place : AppPlace.values()) {
			if (token.matches(place.name() + "(;[^;]*)*")) {
				return true;
			}
		}
		return false;
	}
}
