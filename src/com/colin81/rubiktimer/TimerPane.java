package com.colin81.rubiktimer;

import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;

import net.miginfocom.swing.MigLayout;

import com.colin81.rubiktimer.scramblers.Scrambler;

public class TimerPane extends JPanel implements KeyEventDispatcher {

	private static final long serialVersionUID = -6280966529253447993L;

	private ButtonTimer btnTimer;
	private JButton btnKeep;
	private JButton btnDiscard;

	private JLabel lblScrambleData;

	private Scrambler scrambler;
	private Profile profile;

	/**
	 * Sandbox mode is a free form timing mode for when no solving profile is
	 * specified. It lacks generating scrambles and saving times.
	 */
	private final boolean sandbox = false;

	public TimerPane(final Profile profile) {
		final KeyboardFocusManager manager = KeyboardFocusManager
				.getCurrentKeyboardFocusManager();
		manager.addKeyEventDispatcher(this);
		setProfile(profile);
		/*
		 * if (profile != null) { sandbox = false;
		 * 
		 * buildPane(); requestNewScramble(); } else { sandbox = true;
		 * buildSandBoxPane(); }
		 */

	}

	private void buildPane() {
		setLayout(new MigLayout("", "[grow 60][grow 40]",
				"[][grow][grow 40][][]"));

		final JLabel lblScramble = new JLabel("Scramble");
		add(lblScramble, "cell 0 0");

		final JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, "cell 1 0 1 3,width min(400),grow");

		final JList<String> list = new JList<String>();
		scrollPane.setViewportView(list);

		final JLabel lblTimes = new JLabel("Times");
		scrollPane.setColumnHeaderView(lblTimes);

		lblScrambleData = new JLabel("<some scramble/>");
		add(lblScrambleData, "cell 0 0");

		btnKeep = new JButton("Keep");
		btnDiscard = new JButton("Discard");
		btnTimer = new ButtonTimer();

		btnKeep.setEnabled(false);
		btnKeep.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				btnKeep.setEnabled(false);
				btnDiscard.setEnabled(false);
				// saveSolve(btnTimer.getTime(), btnTimer.getDate(),
				// lblScramble.getText());
			}

		});
		add(btnKeep,
				"flowx,cell 0 2,width max(200),alignx center,aligny center");

		btnDiscard.setEnabled(false);
		btnDiscard.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent arg0) {
				btnTimer.reset();
				btnDiscard.setEnabled(false);
				btnKeep.setEnabled(false);
			}

		});
		add(btnDiscard, "cell 0 2,width max(200),alignx center,aligny center");

		add(btnTimer,
				"cell 0 1,width max(400, 80%),alignx center,height max(300, 40%),aligny center");

		final JLabel lblStatistics = new JLabel("Statistics");
		add(lblStatistics, "flowx,cell 0 3");

		final JSeparator separator_4 = new JSeparator();
		add(separator_4, "cell 0 3,growx");

		final JSeparator separator_5 = new JSeparator();
		add(separator_5, "cell 1 3,growx");
	}

	/**
	 * This includes only the simple button timer.
	 */
	private void buildSandBoxPane() {
		setLayout(new MigLayout("", "[grow]"));

		btnTimer = new ButtonTimer();
		add(btnTimer,
				"cell 0 0,width max(400, 80%),alignx center,height max(300, 40%),aligny center");

	}

	/**
	 * Handles keyboard events over the whole window. Used for starting and
	 * stopping the main timer.
	 */
	@Override
	public boolean dispatchKeyEvent(final KeyEvent e) {
		if (sandbox) {
			if (e.getKeyCode() == KeyEvent.VK_SPACE
					&& e.getID() == KeyEvent.KEY_PRESSED) {
				btnTimer.startStop(e);
			}
		} else {
			if (e.getKeyCode() == KeyEvent.VK_SPACE
					&& e.getID() == KeyEvent.KEY_PRESSED) {
				btnTimer.startStop(e);
				if (btnTimer.isFinished()) {
					btnKeep.setEnabled(true);
					btnDiscard.setEnabled(true);
				} else {
					requestNewScramble();
					btnKeep.setEnabled(false);
					btnDiscard.setEnabled(false);
				}
			}
		}
		return false;
	}

	public void requestNewScramble() {
		if (scrambler != null) {
			lblScrambleData.setText(scrambler.getNewScramble(scrambler
					.getDefaultSize()));
		} else {
			lblScrambleData.setText("No scramble available!");
		}

	}

	public void setProfile(final Profile profile) {
		this.removeAll();
		if (profile == null) {
			buildSandBoxPane();
		} else {
			buildPane();
			this.profile = profile;
			this.scrambler = profile.getPuzzle().getScramblerObject();
		}

	}
}
