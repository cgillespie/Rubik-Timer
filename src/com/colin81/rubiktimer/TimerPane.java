package com.colin81.rubiktimer;

import java.awt.Component;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import net.miginfocom.swing.MigLayout;

import com.colin81.rubiktimer.scramblers.Scrambler;

public class TimerPane extends JPanel implements KeyEventDispatcher {

	private static Logger LOGGER = Logger.getLogger(TimerPane.class.getName());

	private static final long serialVersionUID = -6280966529253447993L;

	private ButtonTimer btnTimer;
	private JButton btnKeep;
	private JButton btnDiscard;

	private JLabel lblScrambleData;
	private JScrollPane scrollPane;
	/**
	 * This panel is for displaying SolveTimeCell objects in a list like manner.
	 * 
	 * @see Solve
	 * @see SolveTimeCell
	 * @see JScrollPane
	 */
	private JPanel listPane;
	private final ContainerListener listPaneListener = new ContainerListener() {

		@Override
		public void componentAdded(final ContainerEvent e) {
			// Is this worth doing when all components are initially added?
			// TimerPane.this.updateStatistics();

		}

		@Override
		public void componentRemoved(final ContainerEvent e) {
			TimerPane.this.updateStatistics();
			((Component) e.getSource()).repaint();
		}

	};

	private final StorageInterface db;
	private Scrambler scrambler;
	private Profile profile;

	private volatile List<Solve> solves;

	/**
	 * Sandbox mode is a free form timing mode for when no solving profile is
	 * specified. It lacks generating scrambles and saving times.
	 */
	private boolean sandbox = false;

	private JLabel lblBesttime;
	private JLabel lblWorstTime;
	private JLabel lblNumberSolves;
	private JLabel lblAveragetime;
	private JLabel lblAverageof5Time;
	private JLabel lblAverageOf12Time;

	public TimerPane(final StorageInterface db, final Profile profile) {
		this.db = db;
		final KeyboardFocusManager manager = KeyboardFocusManager
				.getCurrentKeyboardFocusManager();
		manager.addKeyEventDispatcher(this);
		setProfile(profile);
	}

	private void buildPane() {
		setLayout(new MigLayout("", "[grow][grow 40]",
				"[][grow][grow 40][grow]"));

		final JLabel lblScramble = new JLabel("Scramble");
		add(lblScramble, "cell 0 0");

		scrollPane = new JScrollPane();
		add(scrollPane, "cell 1 0 1 4,width min(400),grow");

		final JLabel lblTimes = new JLabel("Times");
		scrollPane.setColumnHeaderView(lblTimes);

		listPane = new JPanel();
		listPane.setLayout(new BoxLayout(listPane, BoxLayout.Y_AXIS));
		scrollPane.setViewportView(listPane);

		lblScrambleData = new JLabel();
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
				saveSolve(btnTimer.getTime(), btnTimer.getDate(),
						lblScramble.getText());
			}

		});

		final Component horizontalGlue_1 = Box.createHorizontalGlue();
		add(horizontalGlue_1, "flowx,cell 0 2,growx");
		add(btnKeep, "cell 0 2,width max(200),alignx center,growy");

		btnDiscard.setEnabled(false);
		btnDiscard.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent arg0) {
				btnTimer.reset();
				btnDiscard.setEnabled(false);
				btnKeep.setEnabled(false);
			}

		});

		final Component horizontalGlue = Box.createHorizontalGlue();
		add(horizontalGlue, "cell 0 2,growx");
		add(btnDiscard, "cell 0 2,width max(200),alignx center,growy");

		add(btnTimer,
				"cell 0 1,width max(400, 80%),alignx center,height max(300, 40%),aligny center");

		final JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED,
				null, null), "Statistics", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));
		add(panel, "cell 0 3,growx,aligny bottom");
		panel.setLayout(new MigLayout("", "[][][grow][][]", "[][][]"));

		final JLabel lblBest = new JLabel("Best:");
		panel.add(lblBest, "cell 0 0,alignx right");

		lblBesttime = new JLabel();
		panel.add(lblBesttime, "cell 1 0");

		final Component horizontalGlue_3 = Box.createHorizontalGlue();
		panel.add(horizontalGlue_3, "cell 2 0,growx");

		final JLabel lblAverage = new JLabel("Average:");
		panel.add(lblAverage, "cell 3 0,alignx right");

		lblAveragetime = new JLabel();
		panel.add(lblAveragetime, "cell 4 0");

		final JLabel lblWorst = new JLabel("Worst:");
		panel.add(lblWorst, "cell 0 1,alignx right");

		lblWorstTime = new JLabel();
		panel.add(lblWorstTime, "cell 1 1");

		final JLabel lblAverageOf5 = new JLabel("Average of 5:");
		panel.add(lblAverageOf5, "cell 3 1, alignx right");

		lblAverageof5Time = new JLabel();
		panel.add(lblAverageof5Time, "cell 4 1");

		final JLabel lblNumber = new JLabel("Number:");
		panel.add(lblNumber, "cell 0 2");

		lblNumberSolves = new JLabel();
		panel.add(lblNumberSolves, "cell 1 2");

		final JLabel lblAverageOf12 = new JLabel("Average of 12:");
		panel.add(lblAverageOf12, "cell 3 2");

		lblAverageOf12Time = new JLabel("");
		panel.add(lblAverageOf12Time, "cell 4 2, alignx right");

		final Component horizontalGlue_2 = Box.createHorizontalGlue();
		add(horizontalGlue_2, "cell 0 2,growx");
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
				btnTimer.startStop();
			}
		} else {
			if (e.getKeyCode() == KeyEvent.VK_SPACE
					&& e.getID() == KeyEvent.KEY_PRESSED) {
				btnTimer.startStop();
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

	/**
	 * Adds a solve object to our own list like implementation.
	 * 
	 * @param s
	 *            The solve object to model
	 * @param index
	 *            The child index of the listPane container.
	 * @see Solve
	 */
	private void listPaneAdd(final Solve s, final int index) {
		final SolveTimeCell stc = new SolveTimeCell(s, db);
		if (index == -1) {
			listPane.add(stc);
		} else {
			listPane.add(stc, index);
		}
	}

	public void requestNewScramble() {
		if (scrambler != null) {
			lblScrambleData.setText(scrambler.getNewScramble(scrambler
					.getDefaultSize()));
		} else {
			lblScrambleData.setText("No scramble available!");
		}

	}

	/**
	 * Saves the solve to via the storage interface and updates the relevant
	 * visual sections.
	 * 
	 * @param time
	 *            The solve time in milliseconds
	 * @param date
	 *            The date in milliseconds since epoch
	 * @param scramble
	 *            The text scramble used for that solve
	 */
	private void saveSolve(final long time, final long date,
			final String scramble) {

		final Solve s = new Solve();
		s.setProfile(profile);
		s.setSolveTime(time);
		s.setDateTime(date);
		s.setScramble(scramble);

		try {
			s.setId(db.addSolve(s));
			this.listPaneAdd(s, 0);
			updateStatistics();
		} catch (final Exception e) {
			e.printStackTrace();
			LOGGER.severe(e.getLocalizedMessage());
		}
	}

	public void setInspectionTime(final int inspectionTime) {
		this.btnTimer.setInspectionTime(inspectionTime);
	}

	public void setProfile(final Profile profile) {
		this.removeAll();
		if (profile == null) {
			sandbox = true;
			buildSandBoxPane();
		} else {
			sandbox = false;
			this.profile = profile;
			this.scrambler = profile.getPuzzle().getScramblerObject();

			buildPane();
			updateSolves();
			requestNewScramble();
		}

	}

	private void updateSolves() {
		try {
			// Comment out this next one if you want to see a StackOverflow
			listPane.removeContainerListener(listPaneListener);
		} catch (final Exception e) {
			e.printStackTrace();
		}
		listPane.removeAll();
		listPane.addContainerListener(listPaneListener);
		new Thread() {

			@Override
			public void run() {
				try {
					updateStatistics();
					RubikTimer.setInfo("Loading times...");
					int done = 0;
					final int size = solves.size();
					for (int i = 0; i < solves.size(); i++) {
						listPaneAdd(solves.get(i), -1);
						done++;
						final double percent = (((done * 1.0) / size) * 100.0);
						RubikTimer.setProgress((int) percent);
					}
					RubikTimer.setProgress(0);
					RubikTimer.setInfo("Done");

				} catch (final Exception e) {
					LOGGER.severe(e.getLocalizedMessage());
					e.printStackTrace();
				}
			}

		}.start();

	}

	private void updateStatistics() {
		try {
			solves = db.getSolves(profile);
			final Solve fastest = db.getFastestSolve(profile);
			long time = fastest == null ? 0 : fastest.getSolveTime();
			lblBesttime.setText(Utils.milliFormat(time));

			final Solve slowest = db.getSlowestSolve(profile);
			time = slowest == null ? 0 : slowest.getSolveTime();
			lblWorstTime.setText(Utils.milliFormat(time));
		} catch (final Exception e) {
			e.printStackTrace();
		}
		final long average = Utils.averageSolveTime(solves, 0);
		lblAveragetime.setText(Utils.milliFormat(average));

		final long average5 = Utils.averageSolveTime(solves, 5);
		lblAverageof5Time.setText(Utils.milliFormat(average5));

		final long average12 = Utils.averageSolveTime(solves, 12);
		lblAverageOf12Time.setText(Utils.milliFormat(average12));

		lblNumberSolves.setText(String.valueOf(solves.size()));
	}
}