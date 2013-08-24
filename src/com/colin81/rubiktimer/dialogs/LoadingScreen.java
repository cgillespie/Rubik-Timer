package com.colin81.rubiktimer.dialogs;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import net.miginfocom.swing.MigLayout;

public class LoadingScreen extends JFrame {

	private static final long serialVersionUID = -1095428767433644369L;

	private final JLabel lblLoadingDetails;
	private final JProgressBar progressBar;
	private final int max;

	public LoadingScreen() {
		this.setLocationByPlatform(true);
		setSize(450, 300);
		setResizable(false);
		setTitle("Loading Appname");
		getContentPane().setLayout(new MigLayout("", "[grow]", "[grow][][]"));

		final JPanel panel = new JPanel();
		getContentPane().add(panel, "cell 0 0,grow");

		lblLoadingDetails = new JLabel("Loading Details:");
		getContentPane().add(lblLoadingDetails, "cell 0 1");

		progressBar = new JProgressBar();
		max = progressBar.getWidth();
		progressBar.setMinimum(0);
		progressBar.setMaximum(max);
		getContentPane().add(progressBar, "cell 0 2,growx");

	}

	public void setLoadingMessage(final String message) {
		lblLoadingDetails.setText(message);
	}

	public void setProgress(final double percent) {
		progressBar.setValue((int) (max / percent));
	}

}
