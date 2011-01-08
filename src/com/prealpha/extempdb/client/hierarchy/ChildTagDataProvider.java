/*
 * ChildTagDataProvider.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.client.hierarchy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import com.google.common.collect.SetMultimap;
import com.google.gwt.view.client.AbstractDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.inject.Inject;
import com.prealpha.dispatch.shared.DispatcherAsync;
import com.prealpha.extempdb.client.error.ManagedCallback;
import com.prealpha.extempdb.shared.action.GetHierarchy;
import com.prealpha.extempdb.shared.action.GetHierarchyResult;
import com.prealpha.extempdb.shared.action.GetTag;
import com.prealpha.extempdb.shared.action.GetTagResult;
import com.prealpha.extempdb.shared.dto.TagDto;
import com.prealpha.extempdb.shared.id.TagName;

public class ChildTagDataProvider extends AbstractDataProvider<TagDto> {
	private static SetMultimap<TagName, TagName> hierarchy;

	public static boolean isKnownLeaf(TagName tagName) {
		if (hierarchy == null) {
			return false;
		} else if (!hierarchy.containsKey(tagName)) {
			return true;
		} else {
			return hierarchy.get(tagName).isEmpty();
		}
	}

	private final DispatcherAsync dispatcher;

	private TagDto parent;

	private boolean initialized = false;

	@Inject
	public ChildTagDataProvider(DispatcherAsync dispatcher) {
		this.dispatcher = dispatcher;
	}

	public boolean isInitialized() {
		return initialized;
	}

	public void init(TagDto parent) {
		if (initialized) {
			throw new IllegalStateException();
		} else {
			this.parent = parent;
			initialized = true;
		}
	}

	@Override
	protected void onRangeChanged(final HasData<TagDto> display) {
		if (!initialized) {
			throw new IllegalStateException();
		}

		dispatcher.execute(new GetHierarchy(),
				new ManagedCallback<GetHierarchyResult>() {
					@Override
					public void onSuccess(GetHierarchyResult result) {
						hierarchy = result.getHierarchy();
						Set<TagName> children;

						if (parent == null) {
							children = hierarchy.get(null);
						} else {
							children = hierarchy.get(new TagName(parent
									.getName()));
						}

						display.setRowCount(children.size());
						final PendingState pending = new PendingState(display,
								children.size());

						for (TagName name : children) {
							GetTag action = new GetTag(name);
							dispatcher.execute(action,
									new ManagedCallback<GetTagResult>() {
										@Override
										public void onSuccess(
												GetTagResult result) {
											pending.update(result.getTag());
										}
									});
						}
					}
				});
	}

	private class PendingState {
		private final HasData<TagDto> display;

		private final List<TagDto> tags;

		private int count;

		public PendingState(HasData<TagDto> display, int size) {
			this.display = display;
			tags = new ArrayList<TagDto>(size);
			count = size;
		}

		public void update(TagDto tag) {
			tags.add(tag);

			if (--count == 0) {
				// to simplify things for us, we ignore the requested range
				Collections.sort(tags, new Comparator<TagDto>() {
					@Override
					public int compare(TagDto t1, TagDto t2) {
						return t1.getName().compareTo(t2.getName());
					}
				});
				display.setRowData(0, tags);
			}
		}
	}
}
