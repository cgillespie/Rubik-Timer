package com.colin81.rubiktimer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.border.BevelBorder;

import net.miginfocom.swing.MigLayout;

import com.colin81.rubiktimer.dialogs.AboutDialog;
import com.colin81.rubiktimer.dialogs.AboutInfo;
import com.colin81.rubiktimer.dialogs.NewProfileDialog;
import com.colin81.rubiktimer.dialogs.NewPuzzleDialog;

/**
 * @version 0.0.0
 * @author Colin Gillespie
 * 
 */

public class RubikTimer extends JPanel implements ActionListener {

	private static Logger LOGGER = Logger
			.getLogger(DBConnector.class.getName());

	private static final String home = System.getProperty("user.home");
	private static final String delim = System.getProperty("file.separator");

	private static final long serialVersionUID = -5054718029147059487L;

	public static final String TITLE = "Rubik Timer";
	public static final String VERSION = "v0.0.1";
	public static final String COPYRIGHT = "� Copyright Colin Gillespie 2013";
	public static final String DESCRIPTION = "Basic competition style puzzle timer.";
	public static final boolean STABLE = false;

	private List<Puzzle> puzzles;
	private List<Profile> profiles;
	private List<Solve> solves;

	/** Maps menu items for creating a new Profile to its associated Puzzle */
	private Map<JMenuItem, Puzzle> newProfileMenuMap;

	private File dataDir;
	private static final String newProfileCommand = "NEW_PROFILE";
	private DBConnector db;

	private AboutInfo aboutInfo;
	private JMenu mnPuzzle;
	private JMenuItem mntmNewPuzzle;
	private JMenuItem mntmAbout;

	private JLabel lblGeneralInfo;
	private JProgressBar progressBar;

	public RubikTimer() {
		// TODO: show a loading screen here
		try {
			buildUI();
			dataDir = new File(home + delim + ".rubiktimer" + delim);
			dataDir.createNewFile();
			db = new DBConnector(new File(dataDir + "default.db"));
			initPane();
		} catch (final SQLException e) {
			LOGGER.severe(e.getLocalizedMessage());
			// TODO: prompt to create a new db?
			e.printStackTrace();
		} catch (final ClassNotFoundException e) {
			LOGGER.severe(e.getLocalizedMessage());
			// TODO: show error message on the loading screen
			e.printStackTrace();
		} catch (final IOException e) {
			LOGGER.severe(e.getLocalizedMessage());
			// TODO show error message on the loading screen
			e.printStackTrace();
		}

	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		if (e.getActionCommand().equals(newProfileCommand)) {
			addProfile(newProfileMenuMap.get(e.getSource()));
		} else if (e.getSource() == mntmAbout) {
			final AboutDialog ad = new AboutDialog(aboutInfo);
			ad.setVisible(true);
		} else if (e.getSource() == mntmNewPuzzle) {
			addPuzzle();
		}

	}

	private void addProfile(final Puzzle target) {
		LOGGER.info("Creating new Profile for Puzzle: " + target.getName());
		final NewProfileDialog npd = new NewProfileDialog(puzzles);

		if (npd.isConfirmed()) {
			final Profile p = npd.getNewProfile();
			LOGGER.info(p.toString());
			try {
				db.addProfile(p);
				buildPuzzleMenu();
			} catch (final SQLException e) {
				e.printStackTrace();
				LOGGER.severe(e.getLocalizedMessage());
				setInfo(e.getLocalizedMessage());
			}
		}

	}

	private void addPuzzle() {
		LOGGER.info("someone requested a new puzzle");

		final NewPuzzleDialog npd = new NewPuzzleDialog(dataDir + "scramblers");
		if (npd.isConfirmed()) {
			final Puzzle p = npd.getNewPuzzle();
			LOGGER.info(p.toString());
			try {
				db.addPuzzle(p);
				buildPuzzleMenu();
			} catch (final SQLException e) {
				// TODO Auto-generated catch block
				setInfo(e.getLocalizedMessage());
			}
		}

	}

	private void buildPuzzleMenu() throws SQLException {
		mnPuzzle.removeAll();
		mnPuzzle.add(new JSeparator());
		mnPuzzle.add(mntmNewPuzzle);
		puzzles = db.getPuzzles();
		newProfileMenuMap = new HashMap<JMenuItem, Puzzle>(puzzles.size());

		for (int i = 0; i < puzzles.size(); i++) {
			final Puzzle p = puzzles.get(i);
			final JMenu menu = new JMenu(p.getName());
			mnPuzzle.add(menu, i);

			menu.add(new JSeparator());

			final JMenuItem mntmNewProfile = new JMenuItem("New...");
			mntmNewProfile.setIcon(new ImageIcon(RubikTimer.class
					.getResource("/images/new_con.gif")));
			mntmNewProfile.setActionCommand(newProfileCommand);
			mntmNewProfile.addActionListener(this);
			menu.add(mntmNewProfile);

			newProfileMenuMap.put(mntmNewProfile, p);
		}

		try {
			profiles = db.getProfiles();
			for (int i = 0; i < profiles.size(); i++) {
				final Profile p = profiles.get(i);
				final JMenuItem item = new JMenuItem(p.getName());

				item.addActionListener(this);
				final JMenu menu = (JMenu) mnPuzzle.getComponent(p.getPuzzle()
						.getId());
				menu.add(item);
			}
		} catch (final NullPointerException e) {

		}
	}

	private void buildUI() {
		setLayout(new BorderLayout(0, 0));

		final JMenuBar menuBar = new JMenuBar();
		add(menuBar, BorderLayout.NORTH);

		final JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		final JMenuItem mntmNew = new JMenuItem("New...");
		mntmNew.setIcon(new ImageIcon(RubikTimer.class
				.getResource("/images/new_con.gif")));
		mnFile.add(mntmNew);

		final JSeparator separator_1 = new JSeparator();
		mnFile.add(separator_1);

		final JMenuItem mntmDelete = new JMenuItem("Delete...");
		mntmDelete.setIcon(new ImageIcon(RubikTimer.class
				.getResource("/images/delete_obj.gif")));
		mnFile.add(mntmDelete);

		final JSeparator separator_6 = new JSeparator();
		mnFile.add(separator_6);

		final JMenuItem mntmImport = new JMenuItem("Import...");
		mntmImport.setIcon(new ImageIcon(RubikTimer.class
				.getResource("/images/import_wiz.gif")));
		mnFile.add(mntmImport);

		final JMenuItem mntmExport = new JMenuItem("Export...");
		mntmExport.setIcon(new ImageIcon(RubikTimer.class
				.getResource("/images/export_wiz.gif")));
		mnFile.add(mntmExport);

		final JSeparator separator_3 = new JSeparator();
		mnFile.add(separator_3);

		final JMenuItem mntmPreferences = new JMenuItem("Preferences");
		mnFile.add(mntmPreferences);

		final JSeparator separator = new JSeparator();
		mnFile.add(separator);

		final JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
				InputEvent.CTRL_MASK));
		mnFile.add(mntmExit);

		mnPuzzle = new JMenu("Puzzle");
		menuBar.add(mnPuzzle);

		mntmNewPuzzle = new JMenuItem("New...");
		mntmNewPuzzle.setIcon(new ImageIcon(RubikTimer.class
				.getResource("/images/new_con.gif")));
		mntmNewPuzzle.addActionListener(this);
		// mnPuzzle.add(mntmNewPuzzle);

		final Component horizontalGlue = Box.createHorizontalGlue();
		menuBar.add(horizontalGlue);

		final JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);

		mntmAbout = new JMenuItem("About");
		mntmAbout.addActionListener(this);

		final JMenuItem mntmHelp = new JMenuItem("Help");
		mntmHelp.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H,
				InputEvent.CTRL_MASK));
		mntmHelp.setIcon(new ImageIcon(RubikTimer.class
				.getResource("/images/help_contents.gif")));
		mnHelp.add(mntmHelp);

		final JSeparator separator_7 = new JSeparator();
		mnHelp.add(separator_7);
		mnHelp.add(mntmAbout);

		final JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		add(tabbedPane, BorderLayout.CENTER);

		final JPanel panel_1 = new JPanel();
		tabbedPane.addTab("Timer", null, panel_1, null);
		panel_1.setLayout(new MigLayout("", "[grow 60][grow 40]",
				"[][grow][grow 40][][]"));

		final JLabel lblScramble = new JLabel("Scramble");
		panel_1.add(lblScramble, "cell 0 0");

		final JScrollPane scrollPane = new JScrollPane();
		panel_1.add(scrollPane, "cell 1 0 1 3,width min(400),grow");

		final JList<String> list = new JList<String>();
		scrollPane.setViewportView(list);

		final JLabel lblTimes = new JLabel("Times");
		scrollPane.setColumnHeaderView(lblTimes);

		final JButton btnStart = new JButton("Start");
		panel_1.add(btnStart,
				"cell 0 1,width max(400, 80%),alignx center,height max(300, 40%),aligny center");

		final JLabel lblScrambleData = new JLabel("<some scramble/>");
		panel_1.add(lblScrambleData, "cell 0 0");

		final JButton btnKeep = new JButton("Keep");
		btnKeep.setEnabled(false);
		panel_1.add(btnKeep,
				"flowx,cell 0 2,width max(200),alignx center,aligny center");

		final JButton btnDiscard = new JButton("Discard");
		btnDiscard.setEnabled(false);
		panel_1.add(btnDiscard,
				"cell 0 2,width max(200),alignx center,aligny center");

		final JLabel lblStatistics = new JLabel("Statistics");
		panel_1.add(lblStatistics, "flowx,cell 0 3");

		final JSeparator separator_4 = new JSeparator();
		panel_1.add(separator_4, "cell 0 3,growx");

		final JSeparator separator_5 = new JSeparator();
		panel_1.add(separator_5, "cell 1 3,growx");

		final JPanel panel = new JPanel();
		add(panel, BorderLayout.SOUTH);
		panel.setLayout(new BorderLayout(0, 0));

		lblGeneralInfo = new JLabel("Welcome");
		lblGeneralInfo.setBorder(new BevelBorder(BevelBorder.LOWERED, null,
				null, null, null));
		panel.add(lblGeneralInfo);

		progressBar = new JProgressBar();
		panel.add(progressBar, BorderLayout.EAST);

		aboutInfo = new AboutInfo();
		aboutInfo.setAppname(TITLE);
		aboutInfo.setCopyright(COPYRIGHT);
		aboutInfo.setDescription(DESCRIPTION);
		aboutInfo.setVersion(VERSION);

	}

	private void initPane() throws SQLException {
		buildPuzzleMenu();
		solves = db.getSolves();
	}

	private void setInfo(final String msg) {
		lblGeneralInfo.setText(msg);
	}

	private void setProgress(final int percent) {
		progressBar.setValue(percent);
	}
}
