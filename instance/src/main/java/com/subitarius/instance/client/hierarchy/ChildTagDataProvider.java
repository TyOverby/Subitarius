/*
 * ChildTagDataProvider.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.instance.client.hierarchy;

import static com.google.common.base.Preconditions.*;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.gwt.view.client.AbstractDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ProvidesKey;
import com.google.inject.Inject;
import com.prealpha.dispatch.shared.DispatcherAsync;
import com.subitarius.action.GetHierarchy;
import com.subitarius.action.GetHierarchyResult;
import com.subitarius.action.GetTag;
import com.subitarius.action.GetTagResult;
import com.subitarius.action.dto.TagDto;
import com.subitarius.instance.client.error.ManagedCallback;

public final class ChildTagDataProvider extends AbstractDataProvider<TagDto> {
	private static Multimap<String, String> hierarchy;

	public static boolean isKnownLeaf(String tagName) {
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
	private ChildTagDataProvider(DispatcherAsync dispatcher) {
		super(new ProvidesKey<TagDto>() {
			@Override
			public Object getKey(TagDto tag) {
				return (tag == null ? null : tag.getName());
			}
		});
		this.dispatcher = dispatcher;
	}

	public boolean isInitialized() {
		return initialized;
	}

	public void init(TagDto parent) {
		checkState(!initialized);
		this.parent = parent;
		initialized = true;
	}

	@Override
	protected void onRangeChanged(final HasData<TagDto> display) {
		checkState(initialized);
		dispatcher.execute(new GetHierarchy(),
				new ManagedCallback<GetHierarchyResult>() {
					@Override
					public void onSuccess(GetHierarchyResult result) {
						hierarchy = result.getHierarchy();
						Collection<String> children;

						if (parent == null) {
							children = Collections2.filter(hierarchy.keySet(),
									new Predicate<String>() {
										@Override
										public boolean apply(String input) {
											return !hierarchy.values()
													.contains(input);
										}
									});
						} else {
							children = hierarchy.get(parent.getName());
						}

						display.setRowCount(children.size());
						final PendingState pending = new PendingState(display,
								children.size());

						for (String tagName : children) {
							GetTag action = new GetTag(tagName);
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

	private static final class PendingState {
		private final HasData<TagDto> display;

		private final List<TagDto> tags;

		private int count;

		public PendingState(HasData<TagDto> display, int size) {
			this.display = display;
			tags = Lists.newArrayListWithCapacity(size);
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
