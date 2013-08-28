package com.colin81.rubiktimer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.BevelBorder;

import com.colin81.rubiktimer.dialogs.AboutDialog;
import com.colin81.rubiktimer.dialogs.AboutInfo;
import com.colin81.rubiktimer.dialogs.NewProfileDialog;
import com.colin81.rubiktimer.dialogs.NewPuzzleDialog;
import com.colin81.rubiktimer.scramblers.Scrambler;

/**
 * @version 0.0.0
 * @author Colin Gillespie
 * 
 */

public class RubikTimer extends JFrame implements ActionListener {

	private static Logger LOGGER = Logger.getLogger(RubikTimer.class.getName());

	private static final String home = System.getProperty("user.home");
	private static final String delim = System.getProperty("file.separator");

	private static final long serialVersionUID = -5054718029147059487L;

	public static final String TITLE = "Rubik Timer";
	public static final String VERSION = "v0.0.1";
	public static final String COPYRIGHT = "(c) Copyright Colin Gillespie 2013";
	public static final String DESCRIPTION = "Basic competition style puzzle timer.";
	public static final boolean STABLE = false;

	private final String titleString = RubikTimer.TITLE
			+ (RubikTimer.STABLE ? "" : " - " + RubikTimer.VERSION);

	private List<Puzzle> puzzles;
	private List<Profile> profiles;
	/** Maps menu items for creating a new Profile to its associated Puzzle */
	private Map<JMenuItem, Puzzle> newProfileMenuMap;
	private Map<JMenuItem, Profile> profileMenuMap;

	private File dataDir;
	private static final String newProfileCommand = "NEW_PROFILE";
	private static final String setProfileCommand = "SET_PROFILE";

	public static void main(final String[] args) {
		final RubikTimer app = new RubikTimer();
		app.setVisible(true);
	}

	static void setProgress(final int percent) {
		progressBar.setValue(percent);
	}

	private DBConnector db;

	private Profile currentProfile;
	private TimerPane timerPane;
	private AboutInfo aboutInfo;
	private JMenu mnPuzzle;

	private JMenuItem mntmNewPuzzle;
	private JMenuItem mntmAbout;

	private JLabel lblGeneralInfo;

	private static JProgressBar progressBar;

	public RubikTimer() {
		// TODO: show a loading screen here
		// Is a loading screen neccessary?
		// final LoadingScreen loader = new LoadingScreen();
		// loader.setVisible(true);

		initWindow();
		try {

			dataDir = new File(home + delim + ".rubiktimer");
			dataDir.mkdir();

			if (!dataDir.exists()) {
				// use installation directory if home is unavailable
				dataDir = new File("data");
				dataDir.mkdir();
			}
			final File scramblers = new File(dataDir.getAbsolutePath() + delim
					+ "Scramblers");
			final File images = new File(dataDir.getAbsolutePath() + delim
					+ "Images");
			scramblers.mkdir();
			images.mkdir();

			db = new DBConnector(new File(dataDir + delim + "default.db"));

			buildUI();
		} catch (final SQLException e) {
			LOGGER.severe(e.getLocalizedMessage());
			// TODO: prompt to create a new db?
			e.printStackTrace();
		} catch (final ClassNotFoundException e) {
			LOGGER.severe(e.getLocalizedMessage());
			// TODO: show error message on the loading screen
			e.printStackTrace();
		}

		// loader.setVisible(false);
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		if (e.getActionCommand().equals(newProfileCommand)) {
			addProfile(newProfileMenuMap.get(e.getSource()));

		} else if (e.getActionCommand().equals(setProfileCommand)) {
			setProfile(profileMenuMap.get(e.getSource()));

		} else if (e.getSource() == mntmAbout) {
			final AboutDialog ad = new AboutDialog(aboutInfo);
			ad.setVisible(true);

		} else if (e.getSource() == mntmNewPuzzle) {
			addPuzzle();
		}

	}

	private void addProfile(final Puzzle target) {
		LOGGER.info("Creating new Profile for Puzzle: " + target.getName());
		final NewProfileDialog npd = new NewProfileDialog(puzzles, target);

		if (npd.isConfirmed()) {
			final Profile p = npd.getNewProfile();
			LOGGER.info(p.toString());
			try {
				db.addProfile(p);
				buildPuzzleMenu();
			} catch (final Exception e) {
				e.printStackTrace();
				LOGGER.severe(e.getLocalizedMessage());
				setInfo(e.getLocalizedMessage());
			}
		}

	}

	private void addPuzzle() {
		LOGGER.info("someone requested a new puzzle");

		final NewPuzzleDialog npd = new NewPuzzleDialog(
				dataDir.getAbsolutePath());
		if (npd.isConfirmed()) {
			final Puzzle p = npd.getNewPuzzle();
			LOGGER.info(p.toString());
			try {
				db.addPuzzle(p);
				buildPuzzleMenu();
			} catch (final Exception e) {
				// TODO Auto-generated catch block
				setInfo(e.getLocalizedMessage());
			}
		}

	}

	private void buildPuzzleMenu() throws Exception {
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

		profiles = db.getProfiles();
		if (profiles.size() > 0) {
			// TODO load the profile from a preferences file?
			setProfile(profiles.get(0));
		}

		profileMenuMap = new HashMap<JMenuItem, Profile>(profiles.size());

		for (int i = 0; i < profiles.size(); i++) {
			final Profile p = profiles.get(i);
			final JMenuItem item = new JMenuItem(p.getName());
			item.setActionCommand(setProfileCommand);
			item.addActionListener(this);

			final JMenu menu = (JMenu) mnPuzzle.getMenuComponent(p.getPuzzle()
					.getId() - 1);
			menu.add(item, 0);

			profileMenuMap.put(item, p);
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
		mntmExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				System.exit(0);
			}
		});
		mntmExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
				InputEvent.CTRL_MASK));
		mnFile.add(mntmExit);

		mnPuzzle = new JMenu("Puzzle");
		menuBar.add(mnPuzzle);

		mntmNewPuzzle = new JMenuItem("New...");
		mntmNewPuzzle.setIcon(new ImageIcon(RubikTimer.class
				.getResource("/images/new_con.gif")));
		mntmNewPuzzle.addActionListener(this);

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

		timerPane = new TimerPane(db, currentProfile);
		tabbedPane.addTab("Timer", null, timerPane, null);

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

		try {
			buildPuzzleMenu();
		} catch (final Exception e) {
			LOGGER.severe(e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

	private void initWindow() {
		try {
			for (final LookAndFeelInfo info : UIManager
					.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (final Exception e) {
			try {
				UIManager.setLookAndFeel(UIManager
						.getCrossPlatformLookAndFeelClassName());
			} catch (final ClassNotFoundException e1) {
				e1.printStackTrace();
			} catch (final InstantiationException e1) {
				e1.printStackTrace();
			} catch (final IllegalAccessException e1) {
				e1.printStackTrace();
			} catch (final UnsupportedLookAndFeelException e1) {
				e1.printStackTrace();
			}
		}

		setSize(800, 600);
		setLocationRelativeTo(null);
		// only append version if it is an unstable release
		setTitle(titleString);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	/**
	 * Loads a class file from the scrambler directory. The folder structure
	 * must match the package structure of the class file.
	 * 
	 * @param classFile
	 *            The fully qualified class name of the scrambler
	 * @return A new instance of the specified scrambler
	 * @see Scrambler
	 */
	private Scrambler loadScrambler(final String classFile) {
		try {
			final File classesDir = new File(dataDir + delim + "Scramblers");
			final ClassLoader parentLoader = Scrambler.class.getClassLoader();

			final URLClassLoader loader = new URLClassLoader(
					new URL[] { classesDir.toURI().toURL() }, parentLoader);
			final Class<?> _class = loader.loadClass(classFile);
			final Scrambler scrambler = (Scrambler) _class.newInstance();
			loader.close();
			return scrambler;

		} catch (final ClassCastException e) {
			LOGGER.severe("Scrambler is invalid or doesn't exists: "
					+ e.getLocalizedMessage());
			setInfo("Scrambler is invalid or doesn't exists!");
			e.printStackTrace();
		} catch (final ClassNotFoundException e) {
			e.printStackTrace();
		} catch (final MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;

	}

	private void setInfo(final String msg) {
		lblGeneralInfo.setText(msg);
	}

	private void setProfile(final Profile profile) {
		currentProfile = profile;
		currentProfile.getPuzzle().setScramblerObject(
				loadScrambler(currentProfile.getPuzzle().getScrambler()));

		// TODO make this argument dynamic!
		currentProfile.setInspectionTime(15);

		timerPane.setProfile(currentProfile);

		setTitle(currentProfile.getName() + " - "
				+ currentProfile.getPuzzle().getName() + "\t\t" + titleString);
	}
}
