package com.prealpha.extempdb.instance.ui;

import javax.swing.JLabel;
import javax.swing.JProgressBar;

public class ProgressMessage extends Message {
	private static final long serialVersionUID = 1L;
	
	
	private String title; 
	private int progress;
	private int status;
	
	private JLabel titleLabel;
	private JProgressBar bar;
	
	public ProgressMessage(String title){
		this.status=Message.STATUS_OK;
		this.setLayout(null);
		
		this.titleLabel=new JLabel(title);
		this.titleLabel.setBounds(9, 0, 300, 40);
		this.add(titleLabel);
		
		this.bar=new JProgressBar();
		this.bar.setBounds(9, 43, 400, 30);
		this.add(this.bar);
	}
	public ProgressMessage(String smallMessage,int status){
		this(smallMessage);
		this.status=status;
	}

	@Override
	public int getHeight() {
		return 100;
	}

	@Override
	public int getStatus() {
		return this.status;
	}
	
	public void setPercent(int amount){
		this.progress=amount;
		this.bar.setValue(this.progress);
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
