package com.prealpha.extempdb.instance.launcher.ui;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class PanelMessage extends Message {
	private static final long serialVersionUID = 1L;
	
	private String title;
	private JPanel referencedPanel;
	private int status;
	
	private JLabel titleLabel;
	private int height=40;

	public PanelMessage(String title,JPanel panel){
		this.title=title;
		this.referencedPanel=panel;
		
		this.titleLabel=new JLabel(this.title);
		this.titleLabel.setBounds(9, 0, 300, 40);
		this.add(titleLabel);
		
		this.referencedPanel.setBounds(9, 43, this.referencedPanel.getWidth(), this.referencedPanel.getHeight());
		this.add(this.referencedPanel);
	}
	public PanelMessage(String title,JPanel panel,int status){
		this(title,panel);
		this.status=status;
	}
	
	@Override
	public int getHeight() {
		return this.height;
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
