/*
 * LauncherUi.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.launcher;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.google.inject.Inject;

final class LauncherUi implements WindowListener {
	private final ResourceBundle resourceBundle;
	private final InstanceServer instanceServer;
	private final JFrame frame;

	@Inject
	private LauncherUi(ResourceBundle resourceBundle,
			final InstanceServer instanceServer) {
		this.resourceBundle = resourceBundle;
		this.instanceServer = instanceServer;

		frame = new JFrame(this.resourceBundle.getString("frame.title"));
		JButton button = new JButton(
				this.resourceBundle.getString("frame.button"));
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				stopServer();
			}
		});

		frame.addWindowListener(this);
		frame.add(button);
		frame.setSize(250, 100);
	}

	private void stopServer() {
		try {
			instanceServer.stop();
			frame.setVisible(false);
			frame.dispose();
		} catch (InstanceServerException isx) {
			handleException(isx);
		}
	}

	void show() {
		frame.setVisible(true);
	}

	void handleException(Throwable caught) {
		JOptionPane.showMessageDialog(null,
				resourceBundle.getObject("error.title"), caught.toString(),
				JOptionPane.ERROR_MESSAGE);
	}

	@Override
	public void windowClosing(WindowEvent e) {
		stopServer();
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}
}
