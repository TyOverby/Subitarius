/*
 * Launcher.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.prealpha.extempdb.instance.launcher;

import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.prealpha.extempdb.instance.launcher.ui.MainWindow;

final class Launcher {
	public static void main(String[] args) {
		Injector injector = Guice.createInjector(new JpaPersistModule(
				"instance"), new LauncherModule());
		Launcher launcher = injector.getInstance(Launcher.class);
		launcher.launch();
	}

	private final MainWindow mainWindow;

	private final PersistService persistService;

	@Inject
	private Launcher(MainWindow mainWindow, PersistService persistService) {
		this.mainWindow = mainWindow;
		this.persistService = persistService;
	}

	private void launch() {
		persistService.start();
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				mainWindow.enable();
			}
		});
		
		mainWindow.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent event) {
				persistService.stop();
			}
		});
	}
}
