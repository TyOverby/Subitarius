package com.prealpha.extempdb.instance.launcher.ui;

import javax.swing.JPanel;
import javax.swing.JLabel;

import java.awt.Desktop;
import java.awt.Font;
import java.awt.Color;
import javax.swing.JButton;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;

public class ControlPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private boolean serverRunning = false; //TODO: make this dependant.
	private URI uri;
	private JLabel lbllink;
	private JLabel lblRunning;
	private JTextField passField;
	private JButton btnStartStop;

	/**
	 * Create the panel.
	 */
	public ControlPanel() {
		setLayout(null);

		JLabel lblStatus = new JLabel("Status:");
		lblStatus.setBounds(12, 16, 70, 15);
		add(lblStatus);

		lblRunning = new JLabel("OFF");
		lblRunning.setForeground(Color.RED);
		lblRunning.setFont(new Font("Dialog", Font.BOLD, 20));
		lblRunning.setBounds(15, 21, 97, 42);
		add(lblRunning);

		btnStartStop = new JButton("Start");
		btnStartStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(serverRunning){
					stopServer();
					serverRunning=false;
				}
				else{
					startServer();
					serverRunning=true;
				}
			}
		});
		btnStartStop.setBounds(79, 16, 89, 34);
		add(btnStartStop);

		JLabel lblUrl = new JLabel("Url:");
		lblUrl.setBounds(12, 68, 31, 15);
		add(lblUrl);

		lbllink = new JLabel("<html><a href='#'><u style='color:blue;'>null</u></a></html>");
		lbllink.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if(serverRunning){
					try {
						Desktop.getDesktop().browse(uri);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		lbllink.setBounds(12, 83, 156, 15);
		add(lbllink);

		JLabel lblNewLabel = new JLabel("Password:");
		lblNewLabel.setBounds(12, 118, 100, 15);
		add(lblNewLabel);

		passField = new JTextField();
		passField.setBounds(12, 135, 156, 19);
		passField.setEnabled(false);
		passField.setColumns(10);
		add(passField);
		

	}

	public void setUrl(URI uri){
		this.uri=uri;
		this.lbllink.setText(uri.toASCIIString());
	}

	public void startServer(){
		this.lblRunning.setForeground(Color.green);
		this.lblRunning.setText("ON");
		this.passField.setEnabled(false);
		this.btnStartStop.setText("Stop");
		
		//TODO: actually start the server
	}

	public void stopServer(){
		this.lblRunning.setForeground(Color.red);
		this.lblRunning.setText("OFF");
		this.passField.setEnabled(true);
		this.btnStartStop.setText("Start");
		
		//TODO: actually stop the server
	}

	/**
	 * Gets the password that the user has entered into the password box.
	 */
	public String getPassword(){
		if(this.passField.getText().length()>0){
			return this.passField.getText();
		}
		return null;
	}

	public int getWidth(){
		return 180;
	}
}
