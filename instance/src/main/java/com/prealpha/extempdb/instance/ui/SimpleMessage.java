package com.prealpha.extempdb.instance.ui;

import javax.swing.JLabel;

public class SimpleMessage extends Message {
	private static final long serialVersionUID = 1L;
	
	private String title; 
	private int status;
	
	private JLabel titleLabel;

	public SimpleMessage(String title){
		this.status=Message.STATUS_OK;
		this.setLayout(null);
		
		this.titleLabel=new JLabel(title);
		this.titleLabel.setBounds(9, 0, 300, 40);
		this.add(titleLabel);
	}
	public SimpleMessage(String smallMessage,int status){
		this(smallMessage);
		this.status=status;
	}
	
	@Override
	public int getHeight() {
		return 40;
	}
	@Override
	public int getStatus() {
		return this.status;
	}
	
	public void setTitle(String newTitle){
		this.title=newTitle;
		this.titleLabel=new JLabel(this.title);
		
		this.repaint();
	}
	
	public void setStatus(int newStatus){
		this.status=newStatus;
		
		this.repaint();
	}
}
