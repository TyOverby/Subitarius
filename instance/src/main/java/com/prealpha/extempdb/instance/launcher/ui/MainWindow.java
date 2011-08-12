package com.prealpha.extempdb.instance.launcher.ui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import java.awt.BorderLayout;

public class MainWindow {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow window = new MainWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 644, 449);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JCheckBoxMenuItem chckbxmntmAutomaticallyCheckFor = new JCheckBoxMenuItem("Automatically check for updates");
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
		mnServer.add(mntmStart);
		
		JMenuItem mntmStop = new JMenuItem("Stop");
		mnServer.add(mntmStop);
		
		JMenuItem mntmRestart = new JMenuItem("Restart");
		mnServer.add(mntmRestart);
		
		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);
		
		JMenuItem mntmHelp = new JMenuItem("Help");
		mnHelp.add(mntmHelp);
		
		JMenuItem mntmAbout = new JMenuItem("About");
		mnHelp.add(mntmAbout);
		
		MessageContainer messageContainer = new MessageContainer();
		frame.getContentPane().add(messageContainer, BorderLayout.CENTER);
		
		for(int i=0;i<50;i++){
			messageContainer.addMessage(new SimpleMessage(String.valueOf(i)));
		}
		
		messageContainer.addMessage(new SimpleMessage("test"));
		ProgressMessage pm = new ProgressMessage("test");
		pm.setPercent(50);
		messageContainer.addMessage(pm);
	}

}
