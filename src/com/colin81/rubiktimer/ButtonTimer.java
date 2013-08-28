package com.colin81.rubiktimer;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.Timer;

public class ButtonTimer extends JButton implements ActionListener {
	private static final long serialVersionUID = -7575546735358311744L;

	private final Timer timer;
	private int inspectionTime;
	private long inspectionRemaining;
	private boolean running, inspecting;
	private long startTime;
	private long finishDate;
	private long totalTime;

	public ButtonTimer(final int inspectionTime) {
		super(Utils.milliFormat(0));
		this.inspectionTime = inspectionTime * 1000;
		timer = new Timer(10, this);
		running = false;
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		if (inspecting) {
			inspectionRemaining = ((startTime + inspectionTime) - System
					.currentTimeMillis());
			if (inspectionRemaining <= 0) {
				startStop();
			} else {
				setText(Utils.milliFormat(inspectionRemaining));
			}
		} else {
			totalTime = System.currentTimeMillis() - startTime;
			setText(Utils.milliFormat(totalTime));
		}

	}

	public long getDate() {
		return finishDate;
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

	public boolean isInspecting() {
		return inspecting;
	}

	public boolean isRunning() {
		return running;
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

	public void reset() {
		setText(Utils.milliFormat(0));
		running = false;
		startTime = totalTime = 0;
	}

	public void setInspectionTime(final int inspectionTime) {
		this.inspectionTime = inspectionTime * 1000;
	}

	public void startStop() {
		if (running) {
			timer.stop();
			running = false;
			finishDate = System.currentTimeMillis();
			totalTime = finishDate - startTime;
			setText(Utils.milliFormat(totalTime));
		} else if (inspecting) {
			inspecting = false;
			running = true;
			startTime = System.currentTimeMillis();
		} else {
			inspectionRemaining = inspectionTime;
			inspecting = true;
			timer.start();
			startTime = System.currentTimeMillis();
		}
	}
}
