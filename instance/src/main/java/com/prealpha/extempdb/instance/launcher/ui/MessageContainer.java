package com.prealpha.extempdb.instance.launcher.ui;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.prealpha.extempdb.instance.launcher.Action;

public final class MessageContainer extends JPanel {
	private static final long serialVersionUID = 1L;

	private final Provider<Iterator<Action>> iteratorProvider;

	private List<Message> messages = new ArrayList<Message>(50);
	private final JPanel messageStaging;
	private int shouldHeight = 0;
	private int shouldWidth = 0;

	/**
	 * Create the panel.
	 */
	@Inject
	private MessageContainer(Timer timer,
			Provider<Iterator<Action>> iteratorProvider) {
		this.iteratorProvider = iteratorProvider;

		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		this.messageStaging = new JPanel();
		this.messageStaging
				.setPreferredSize(new Dimension(this.getWidth(), 200));

		JScrollPane scrollPane = new JScrollPane(messageStaging);
		scrollPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		messageStaging.setLayout(null);

		this.add(scrollPane);
		this.addMessage(new SimpleMessage("test"));

		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				Iterator<Action> iterator = MessageContainer.this.iteratorProvider
						.get();
				while (iterator.hasNext()) {
					addMessage(getMessage(iterator.next()));
				}
			}
		}, 0, 5000);
	}

	private Message getMessage(Action action) {
		return new SimpleMessage(action.toString());
	}

	private void addMessage(Message message) {
		this.shouldHeight += message.getHeight();
		this.messageStaging.setPreferredSize(new Dimension(this.getWidth(),
				shouldHeight));

		messages.add(message);
		messageStaging.add(message);
		message.setBounds(0, 0, this.shouldWidth, message.getHeight());

		for (Message m : this.messages) {
			if (!m.equals(message)) {
				m.setBounds(m.getX(), m.getY() + message.getHeight(),
						this.shouldWidth, m.getHeight());
			}
		}
	}

	public void clear() {
		messages.clear();
		messageStaging.removeAll();
	}

	public void onResize(int width) {
		for (Message m : this.messages) {
			m.setSize(width, m.getHeight());
			m.onResize(width);
		}
	}
}
