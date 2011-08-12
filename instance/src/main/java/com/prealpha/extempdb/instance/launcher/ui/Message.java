package com.prealpha.extempdb.instance.launcher.ui;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

public abstract class Message extends JPanel {
	private static final long serialVersionUID = 1L;
	public abstract int getHeight();
	public abstract int getStatus();
	public abstract void onResize(int width);
	
	@Override 
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		if(this.getStatus() == Message.STATUS_OK){
			g.setColor(new Color(Message.STATUS_OK));
		}
		else if(this.getStatus() == Message.STATUS_WARN){
			g.setColor(new Color(Message.STATUS_WARN));
		}
		else if(this.getStatus() == Message.STATUS_ERR){
			g.setColor(new Color(Message.STATUS_ERR));
		}
		
		g.fillRoundRect(3, 3, this.getWidth()-6, this.getHeight()-6, 10, 10);
		g.setColor(Color.black);
		g.drawRoundRect(3, 3, this.getWidth()-6, this.getHeight()-6, 10, 10);
   }
	
	public static final int STATUS_OK=0x99CC99;
	public static final int STATUS_WARN=0xFFFFCC;
	public static final int STATUS_ERR=0xFF6666;
}
