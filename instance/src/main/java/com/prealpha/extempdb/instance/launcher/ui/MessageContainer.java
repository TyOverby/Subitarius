package com.prealpha.extempdb.instance.launcher.ui;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.ScrollPaneConstants;

public class MessageContainer extends JPanel {
	private static final long serialVersionUID = 1L;

	private List<Message> messages = new ArrayList<Message>(50);
	private final JPanel messageStaging;
	private int shouldHeight=0;
	private int shouldWidth=500;
	/**
	 * Create the panel.
	 */
	public MessageContainer() {
		
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		

		this.messageStaging = new JPanel();
		this.messageStaging.setPreferredSize(new Dimension(this.getWidth(),200)); 
		
		JScrollPane scrollPane = new JScrollPane(messageStaging);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		messageStaging.setLayout(null);
		
		this.add(scrollPane);
	}

	public void addMessage(Message message){
		this.shouldHeight+=message.getHeight();
		this.messageStaging.setPreferredSize(new Dimension(this.getWidth(),shouldHeight));
		
		messages.add(message);
		messageStaging.add(message);
		message.setBounds(0, 0, this.shouldWidth, message.getHeight());
	
		for(Message m:this.messages){
			if(!m.equals(message)){
				m.setBounds(m.getX(), m.getY()+message.getHeight(),this.shouldWidth, m.getHeight());
			}
		}
	}

	public void clear(){
		messages.clear();
		messageStaging.removeAll();
	}
}
