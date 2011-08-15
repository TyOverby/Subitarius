/*
 * UserActionContext.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.launcher;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.prealpha.extempdb.util.logging.InjectLogger;

public final class UserActionContext implements Provider<UserAction> {
	@InjectLogger
	private Logger log;
	
	private final Provider<ExecutorService> threadPoolProvider;

	private final Queue<UserAction> actions;

	private volatile UserAction action;

	@Inject
	private UserActionContext(Provider<ExecutorService> threadPoolProvider) {
		this.threadPoolProvider = threadPoolProvider;
		actions = new ConcurrentLinkedQueue<UserAction>();
	}

	@Override
	public UserAction get() {
		return action;
	}

	public void execute(UserAction action) {
		actions.add(action);
		if (this.action == null) {
			ExecutorService threadPool = threadPoolProvider.get();
			runAction(threadPool, actions.remove());
		}
	}

	private void runAction(final ExecutorService threadPool, UserAction action) {
		this.action = action;
		final CountDownLatch latch = new CountDownLatch(action.size());
		for (final Runnable task : action) {
			threadPool.execute(new Runnable() {
				@Override
				public void run() {
					task.run();
					latch.countDown();
				}
			});
		}
		threadPool.execute(new Runnable() {
			@Override
			public void run() {
				try {
					latch.await();
					if (actions.isEmpty()) {
						UserActionContext.this.action = null;
						threadPool.shutdown();
					} else {
						UserAction newAction = actions.remove();
						runAction(threadPool, newAction);
					}
				} catch (InterruptedException ix) {
					threadPool.shutdownNow();
					log.error("unexpected thread pool interrupt", ix);
				}
			}
		});
	}
}
