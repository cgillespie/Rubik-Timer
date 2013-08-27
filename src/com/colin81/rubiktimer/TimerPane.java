package com.colin81.rubiktimer;

import java.awt.Component;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
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
	private JList<Solve> jlistSolves;
	private JScrollPane scrollPane;

	private final StorageInterface db;
	private Scrambler scrambler;
	private Profile profile;
	private DefaultListModel<Solve> listModel;

	/**
	 * Sandbox mode is a free form timing mode for when no solving profile is
	 * specified. It lacks generating scrambles and saving times.
	 */
	private boolean sandbox = false;

	private JLabel lblBesttime;
	private JLabel lblWorsttime;
	private JLabel lblAveragetime;
	private JLabel lblAverageof5Time;

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

		lblScrambleData = new JLabel("<some scramble/>");
		add(lblScrambleData, "cell 0 0");

		btnKeep = new JButton("Keep");
		btnDiscard = new JButton("Discard");
		btnTimer = new ButtonTimer(profile.getInspectionTime());

		btnKeep.setEnabled(false);
		btnKeep.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				btnKeep.setEnabled(false);
				btnDiscard.setEnabled(false);
				saveSolve(btnTimer.getTime(), btnTimer.getDate(),
						lblScramble.getText());
				updateSolves();
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
		panel.setLayout(new MigLayout("", "[][][grow][][]", "[][]"));

		final JLabel lblBest = new JLabel("Best:");
		panel.add(lblBest, "cell 0 0,alignx right");

		lblBesttime = new JLabel();
		panel.add(lblBesttime, "cell 1 0");

		final JLabel lblAverage = new JLabel("Average:");
		panel.add(lblAverage, "cell 3 0,alignx right");

		lblAveragetime = new JLabel();
		panel.add(lblAveragetime, "cell 4 0");

		final JLabel lblWorst = new JLabel("Worst:");
		panel.add(lblWorst, "cell 0 1,alignx right");

		lblWorsttime = new JLabel();
		panel.add(lblWorsttime, "cell 1 1");

		final Component horizontalGlue_3 = Box.createHorizontalGlue();
		panel.add(horizontalGlue_3, "cell 2 1,growx");

		final JLabel lblAverageOf5 = new JLabel("Average of 5:");
		panel.add(lblAverageOf5, "cell 3 1");

		lblAverageof5Time = new JLabel();
		panel.add(lblAverageof5Time, "cell 4 1");

		final Component horizontalGlue_2 = Box.createHorizontalGlue();
		add(horizontalGlue_2, "cell 0 2,growx");
	}

	/**
	 * This includes only the simple button timer.
	 */
	private void buildSandBoxPane() {
		setLayout(new MigLayout("", "[grow]"));

		btnTimer = new ButtonTimer(0);
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

	public void requestNewScramble() {
		if (scrambler != null) {
			lblScrambleData.setText(scrambler.getNewScramble(scrambler
					.getDefaultSize()));
		} else {
			lblScrambleData.setText("No scramble available!");
		}

	}

	private void saveSolve(final long time, final long date,
			final String scramble) {

		final Solve s = new Solve();
		s.setProfile(profile);
		s.setSolveTime(time);
		s.setDateTime(date);
		s.setScramble(scramble);

		try {
			db.addSolve(s);
			listModel.addElement(s);

		} catch (final Exception e) {
			e.printStackTrace();
			LOGGER.severe(e.getLocalizedMessage());
		}
	}

	public void setInspectionTime(final int inspectionTime) {
		this.profile.setInspectionTime(inspectionTime);
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
			final List<Solve> solves = db.getSolves(profile);
			listModel = new DefaultListModel<Solve>();
			for (final Solve s : solves) {
				listModel.addElement(s);
			}
			/* Update List */
			jlistSolves = new JList<Solve>(listModel);

			final ListCellRenderer<Solve> solveRender = new SolveRenderer();
			jlistSolves.setCellRenderer(solveRender);
			jlistSolves.scrollRectToVisible(jlistSolves.getBounds());
			scrollPane.setViewportView(jlistSolves);
			// scrollPane.scrollRectToVisible(getBounds());

			/* Update statistics */
			final Solve fastest = db.getFastestSolve(profile);
			lblBesttime.setText(Utils.milliFormat(fastest.getSolveTime()));

			final Solve slowest = db.getSlowestSolve(profile);
			lblWorsttime.setText(Utils.milliFormat(slowest.getSolveTime()));

			final long average = Utils.averageSolveTime(solves, 0);
			lblAveragetime.setText(Utils.milliFormat(average));

			final long average5 = Utils.averageSolveTime(solves, 5);
			lblAverageof5Time.setText(Utils.milliFormat(average5));

		} catch (final Exception e) {
			LOGGER.severe(e.getLocalizedMessage());
			e.printStackTrace();
		}
	}
}
