/*
 * Launcher.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.launcher;

import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Timer;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.prealpha.extempdb.instance.launcher.ui.MainWindow;

public final class Launcher {
	public static void main(String[] args) {
		Injector injector = Guice.createInjector(new JpaPersistModule(
				"instance"), new LauncherModule());
		Launcher launcher = injector.getInstance(Launcher.class);
		launcher.launch();
	}

	private final PersistService persistService;

	private final Provider<MainWindow> mainWindowProvider;
	
	private final Timer timer;

	@Inject
	private Launcher(PersistService persistService,
			Provider<MainWindow> mainWindowProvider, Timer timer) {
		this.persistService = persistService;
		this.mainWindowProvider = mainWindowProvider;
		this.timer = timer;
	}

	void launch() {
		persistService.start();

		final MainWindow mainWindow = mainWindowProvider.get();
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				mainWindow.realize();
				mainWindow.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosed(WindowEvent event) {
						timer.cancel();
						persistService.stop();
					}
				});
			}
		});
	}
}
