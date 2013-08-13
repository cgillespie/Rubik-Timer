package com.colin81.rubiktimer;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * A basic application launcher for Rubik Timer
 * 
 * @version 0.0.0
 * @author Colin Gillespie
 * 
 */
public class Launcher extends JFrame {

	private static final long serialVersionUID = -460936464092804200L;

	public static void main(final String[] args) {
		final Launcher app = new Launcher();
		app.setVisible(true);
	}

	public Launcher() {
		initWindow();
		final RubikTimer rt = new RubikTimer();
		add(rt);
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
		setTitle(RubikTimer.TITLE
				+ (RubikTimer.STABLE ? "" : " - " + RubikTimer.VERSION));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
}