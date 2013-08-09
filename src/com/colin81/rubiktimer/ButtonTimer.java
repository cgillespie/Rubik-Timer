package com.colin81.rubiktimer;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.Timer;

public class ButtonTimer extends JButton implements ActionListener {
	private static final long serialVersionUID = -7575546735358311744L;

	private final Timer timer;
	private boolean running;
	private long startTime;
	private long finishTime;
	private long totalTime;

	public ButtonTimer() {
		super("00:00:00");
		timer = new Timer(10, this);
		running = false;
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		totalTime = System.currentTimeMillis() - startTime;
		setText(Utils.milliFormat(totalTime));

	}

	public long getDate() {
		return finishTime;
	}

	public long getTime() {
		return totalTime;
	}

	public boolean isFinished() {
		if (!running && totalTime > 0) {
			return true;
		}
		return false;
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

	public void startStop(final KeyEvent e) {
		if (running) {
			timer.stop();
			running = false;
			finishTime = e.getWhen();
			totalTime = finishTime - startTime;
			setText(Utils.milliFormat(totalTime));
		} else {
			running = true;
			timer.start();
			startTime = System.currentTimeMillis();

		}
	}

}
