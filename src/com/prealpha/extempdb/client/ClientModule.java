/*
 * ClientModule.java
 * Copyright (C) 2010 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.client;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.impl.SchedulerImpl;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.inject.BindingAnnotation;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.prealpha.dispatch.client.filter.ActionFilter;
import com.prealpha.dispatch.client.filter.CachingActionFilter;
import com.prealpha.dispatch.client.filter.ChainingFilter;
import com.prealpha.dispatch.client.filter.DelayedBatchingFilter;
import com.prealpha.dispatch.client.filter.DelayedBatchingFilter.BatchDelay;
import com.prealpha.dispatch.client.filter.MergingActionFilter;
import com.prealpha.dispatch.shared.Dispatcher;
import com.prealpha.dispatch.shared.DispatcherAsync;

public final class ClientModule extends AbstractGinModule {
	public ClientModule() {
	}

	@Override
	protected void configure() {
		bind(EventBus.class).to(SimpleEventBus.class).in(Singleton.class);

		bind(HistoryManager.class).in(Singleton.class);
		bind(SessionManager.class).in(Singleton.class);

		bind(ActionFilter.class).annotatedWith(CachingFilter.class)
				.to(CachingActionFilter.class).in(Singleton.class);
		bind(ActionFilter.class).annotatedWith(MergingFilter.class)
				.to(MergingActionFilter.class).in(Singleton.class);
		bind(ActionFilter.class).annotatedWith(BatchingFilter.class)
				.to(DelayedBatchingFilter.class).in(Singleton.class);
		bind(Scheduler.class).to(SchedulerImpl.class).in(Singleton.class);
	}

	@Provides
	@Singleton
	@Inject
	DispatcherAsync getDispatcher(@CachingFilter ActionFilter cachingFilter,
			@MergingFilter ActionFilter mergingFilter,
			@BatchingFilter ActionFilter batchingFilter) {
		DispatcherAsync backingDispatcher = GWT.create(Dispatcher.class);
		((ServiceDefTarget) backingDispatcher)
				.setServiceEntryPoint("./GWT.rpc");

		List<ActionFilter> filters = ImmutableList.of(cachingFilter,
				mergingFilter, batchingFilter);
		ActionFilter dispatcher = new ChainingFilter(filters);
		dispatcher.init(backingDispatcher);
		return dispatcher;
	}

	@Provides
	@BatchDelay
	Integer getBatchDelay() {
		return 200;
	}

	@Provides
	DateTimeFormat getDateTimeFormat() {
		return DateTimeFormat.getFormat("yyyy-MM-dd");
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD })
	@BindingAnnotation
	private static @interface CachingFilter {
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD })
	@BindingAnnotation
	private static @interface MergingFilter {
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD })
	@BindingAnnotation
	private static @interface BatchingFilter {
	}
}
