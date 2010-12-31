/*
 * TagInputSuggestOracle.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.client.common;

import static com.google.common.base.Preconditions.*;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.inject.Inject;
import com.prealpha.extempdb.client.error.ManagedCallback;
import com.prealpha.extempdb.shared.action.GetTagSuggestions;
import com.prealpha.extempdb.shared.action.GetTagSuggestionsResult;
import com.prealpha.extempdb.shared.id.TagName;
import com.prealpha.gwt.dispatch.shared.DispatcherAsync;

public class TagInputSuggestOracle extends SuggestOracle {
	private final DispatcherAsync dispatcher;

	@Inject
	public TagInputSuggestOracle(DispatcherAsync dispatcher) {
		this.dispatcher = dispatcher;
	}

	@Override
	public void requestSuggestions(Request request, Callback callback) {
		GetTagSuggestions action = new GetTagSuggestions(request.getQuery(),
				request.getLimit());
		dispatcher.execute(action, new SuggestionCallback(request, callback));
	}

	private class SuggestionCallback extends
			ManagedCallback<GetTagSuggestionsResult> {
		private final Request request;

		private final Callback callback;

		public SuggestionCallback(Request request, Callback callback) {
			this.request = request;
			this.callback = callback;
		}

		@Override
		public void onSuccess(GetTagSuggestionsResult result) {
			Set<TagName> names = result.getSuggestions();
			Set<Suggestion> suggestions = new HashSet<Suggestion>();

			for (TagName name : names) {
				suggestions.add(new TagSuggestion(name));
			}

			Response response = new Response(suggestions);
			callback.onSuggestionsReady(request, response);
		}
	}

	private static class TagSuggestion implements Suggestion {
		private final TagName tagName;

		public TagSuggestion(TagName tagName) {
			checkNotNull(tagName);
			this.tagName = tagName;
		}

		@Override
		public String getDisplayString() {
			return tagName.getName();
		}

		@Override
		public String getReplacementString() {
			return tagName.getName();
		}
	}
}
