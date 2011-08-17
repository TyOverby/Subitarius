package com.prealpha.extempdb.instance.launcher.ui;

import static com.google.common.base.Preconditions.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSplitPane;

import com.google.inject.Inject;

public final class MainWindow {
	private final MessageContainer messageContainer;

	private boolean initialized;
	private JFrame frame;
	private JSplitPane splitPane;
	private ControlPanel controlPanel;

	/**
	 * Create the application.
	 */
	@Inject
	private MainWindow(MessageContainer messageContainer) {
		this.messageContainer = messageContainer;
	}

	/**
	 * Initialize the contents of the frame.
	 */
	public void initialize() {
		checkState(!initialized);
		initialized = true;

		frame = new JFrame();
		frame.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				onResize();
			}
		});
		frame.setBounds(100, 100, 644, 449);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		JCheckBoxMenuItem chckbxmntmAutomaticallyCheckFor = new JCheckBoxMenuItem(
				"Automatically check for updates");
		mnFile.add(chckbxmntmAutomaticallyCheckFor);

		JMenuItem mntmClose = new JMenuItem("Close");
		mnFile.add(mntmClose);

		JMenu mnLogs = new JMenu("Logs");
		menuBar.add(mnLogs);

		JMenuItem mntmClearDisplay = new JMenuItem("Clear Display");
		mnLogs.add(mntmClearDisplay);

		JMenuItem mntmSend = new JMenuItem("Send");
		mnLogs.add(mntmSend);

		JMenu mnActions = new JMenu("Actions");
		menuBar.add(mnActions);

		JMenuItem mntmImportDeltas = new JMenuItem("Import deltas");
		mnActions.add(mntmImportDeltas);

		JMenuItem mntmExportDeltas = new JMenuItem("Export deltas");
		mnActions.add(mntmExportDeltas);

		JMenu mnServer = new JMenu("Server");
		menuBar.add(mnServer);

		JMenuItem mntmStart = new JMenuItem("Start");
		mntmStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controlPanel.startServer();
			}
		});
		mnServer.add(mntmStart);

		JMenuItem mntmStop = new JMenuItem("Stop");
		mntmStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controlPanel.stopServer();
			}
		});
		mnServer.add(mntmStop);

		JMenuItem mntmRestart = new JMenuItem("Restart");
		mntmRestart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controlPanel.stopServer();
				controlPanel.startServer();
			}
		});
		mnServer.add(mntmRestart);

		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);

		JMenuItem mntmHelp = new JMenuItem("Help");
		mnHelp.add(mntmHelp);

		JMenuItem mntmAbout = new JMenuItem("About");
		mnHelp.add(mntmAbout);

		splitPane = new JSplitPane();
		splitPane.setEnabled(false);
		splitPane.setDividerSize(0);
		frame.getContentPane().add(splitPane, BorderLayout.CENTER);

		splitPane.setLeftComponent(messageContainer);

		controlPanel = new ControlPanel();
		controlPanel.setForeground(Color.BLUE);
		splitPane.setRightComponent(controlPanel);

	}

	public void enable() {
		checkState(initialized);
		frame.setVisible(true);
		onResize();
	}

	private void onResize() {
		checkState(initialized);
		this.splitPane.setDividerLocation(this.splitPane.getWidth()
				- this.controlPanel.getWidth());
		this.messageContainer.onResize(this.splitPane.getWidth()
				- this.controlPanel.getWidth() - 23);
	}

	public void addWindowListener(WindowListener listener) {
		frame.addWindowListener(listener);
	}

	public void removeWindowListener(WindowListener listener) {
		frame.removeWindowListener(listener);
	}
}
