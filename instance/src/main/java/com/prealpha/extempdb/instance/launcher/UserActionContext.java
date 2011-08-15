/*
 * UserActionContext.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.launcher;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.prealpha.extempdb.util.logging.InjectLogger;

public final class UserActionContext implements Provider<UserAction> {
	@InjectLogger
	private Logger log;

	private final Provider<ExecutorService> threadPoolProvider;

	private final Queue<UserAction> actions;

	private volatile UserAction action;

	private final List<UserActionListener> listeners;

	private final ReadWriteLock listenerLock;

	@Inject
	private UserActionContext(Provider<ExecutorService> threadPoolProvider) {
		this.threadPoolProvider = threadPoolProvider;
		actions = new ConcurrentLinkedQueue<UserAction>();
		listeners = Lists.newArrayList();
		listenerLock = new ReentrantReadWriteLock();
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
		final int total = action.size();
		final CountDownLatch latch = new CountDownLatch(total);
		for (final Runnable task : action) {
			threadPool.execute(new Runnable() {
				@Override
				public void run() {
					task.run();
					latch.countDown();
					fireProgress(total - (int) latch.getCount(), total);
				}
			});
		}
		threadPool.execute(new Runnable() {
			@Override
			public void run() {
				try {
					latch.await();
					fireComplete();
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

	private void fireProgress(int complete, int total) {
		listenerLock.readLock().lock();
		for (UserActionListener listener : listeners) {
			listener.onActionProgress(action, complete, total);
		}
		listenerLock.readLock().unlock();
	}

	private void fireComplete() {
		listenerLock.readLock().lock();
		for (UserActionListener listener : listeners) {
			listener.onActionComplete(action);
		}
		listenerLock.readLock().unlock();
	}

	public void addUserActionListener(UserActionListener listener) {
		listenerLock.writeLock().lock();
		listeners.add(listener);
		listenerLock.writeLock().unlock();
	}

	public void removeUserActionListener(UserActionListener listener) {
		listenerLock.writeLock().lock();
		listeners.remove(listener);
		listenerLock.writeLock().unlock();
	}
}
