package com.colin81.rubiktimer;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.Timer;

public class ButtonTimer extends JButton implements ActionListener {
	private static final long serialVersionUID = -7575546735358311744L;

	private boolean running;

	public ButtonTimer() {
		super("00:00:00");
		running = false;
	}

	@Override
	public void actionPerformed(final ActionEvent arg0) {
		if (running) {
			running = false;
		} else {
			running = true;
			final long startTime = System.currentTimeMillis();
			final Timer timer = new Timer(10, new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent arg0) {
					final long now = System.currentTimeMillis();
					ButtonTimer.this.setText("");

				}

			});
		}

	}

	@Override
	protected void paintComponent(final Graphics g) {
		final int h = this.getHeight();
		final double resizal = ((double) h) / 36;

		final String t = getText();
		setText("<html><span style='font-size:" + (resizal * 11) + "'>" + t);
		super.paintComponent(g);
		setText(t);
	}

	private String timeFormat(final long time) {
		return "";
	}

}
