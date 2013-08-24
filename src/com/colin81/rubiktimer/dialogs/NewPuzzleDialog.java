package com.colin81.rubiktimer.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.miginfocom.swing.MigLayout;

import com.colin81.rubiktimer.DBConnector;
import com.colin81.rubiktimer.Puzzle;
import com.colin81.rubiktimer.Utils;

public class NewPuzzleDialog extends JDialog implements ActionListener {

	private static Logger LOGGER = Logger
			.getLogger(DBConnector.class.getName());

	private static final long serialVersionUID = 5219411011556348152L;

	private final JButton btnChooseScrambler;
	private final JButton btnChooseImage;
	private final JTextField textField;
	private final JButton btnOk;
	private final JButton btnCancel;
	private final JLabel lblScramblePath;
	private final JLabel lblImagePath;
	private JFileChooser chooser;

	private final String dir;
	private String scramblerName;
	private String imageLoc;
	private Puzzle puzzle = null;
	private boolean confirmed;

	public NewPuzzleDialog(final String dataDir) {
		setMinimumSize(new Dimension(300, 150));
		this.setModal(true);
		setLocationRelativeTo(null);
		// setResizable(false);
		// setSize(320, 140);

		this.dir = dataDir;
		setTitle("Add New Puzzle");
		getContentPane().setLayout(new BorderLayout(0, 0));

		final JPanel panel = new JPanel();
		getContentPane().add(panel);
		panel.setLayout(new MigLayout("", "[][grow]", "[][][][][]"));

		final JLabel lblPuzzleName = new JLabel("Puzzle Name");
		panel.add(lblPuzzleName, "cell 0 0,alignx trailing");

		textField = new JTextField();
		panel.add(textField, "cell 1 0,growx");
		textField.setColumns(10);

		final JLabel lblScrambler = new JLabel("Scrambler");
		panel.add(lblScrambler, "cell 0 1,alignx trailing");

		btnChooseScrambler = new JButton("");
		btnChooseScrambler.addActionListener(this);
		btnChooseScrambler.setBorder(null);
		btnChooseScrambler.setIcon(new ImageIcon(NewPuzzleDialog.class
				.getResource("/images/fldr_obj.gif")));
		panel.add(btnChooseScrambler, "flowx,cell 1 1");

		lblScramblePath = new JLabel("<some file>");
		panel.add(lblScramblePath, "cell 1 1");

		final JLabel lblImage = new JLabel("Image");
		panel.add(lblImage, "cell 0 2,alignx trailing");

		btnChooseImage = new JButton("");
		btnChooseImage.addActionListener(this);
		btnChooseImage.setBorder(null);
		btnChooseImage.setIcon(new ImageIcon(NewPuzzleDialog.class
				.getResource("/images/image_obj.gif")));
		panel.add(btnChooseImage, "flowx,cell 1 2");

		final JSeparator separator = new JSeparator();
		panel.add(separator, "cell 0 3 2 1,growx");

		btnOk = new JButton("OK");
		btnOk.addActionListener(this);
		panel.add(btnOk, "flowx,cell 1 4,alignx right");

		lblImagePath = new JLabel("<some image>");
		panel.add(lblImagePath, "cell 1 2");

		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(this);
		panel.add(btnCancel, "cell 1 4");

		setVisible(true);
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		if (e.getSource() == btnCancel) {
			setVisible(false);
			dispose();

		} else if (e.getSource() == btnOk) {
			if (!textField.getText().equals("")) {
				puzzle = new Puzzle(textField.getText());

				puzzle.setScrambler(scramblerName);
				puzzle.setImage(imageLoc);

				confirmed = true;
				setVisible(false);
				dispose();

			} else {
				textField.requestFocus();
				textField.setText("Please enter a name");
				textField.selectAll();
			}
		} else if (e.getSource() == btnChooseScrambler) {
			final String scrambleDir = dir
					+ System.getProperty("file.separator") + "Scramblers";
			LOGGER.info(scrambleDir);
			chooser = new JFileChooser(scrambleDir);

			final FileNameExtensionFilter filter = new FileNameExtensionFilter(
					"Java Class Files", "class");
			chooser.setFileFilter(filter);

			final int returnVal = chooser.showOpenDialog(this);

			if (returnVal == JFileChooser.APPROVE_OPTION) {

				final Path pathAbsolute = Paths.get(chooser.getSelectedFile()
						.getAbsolutePath());
				final Path pathBase = Paths.get(scrambleDir);
				final Path pathRelative = pathBase.relativize(pathAbsolute);

				scramblerName = Utils.pathToQualifiedName(pathRelative
						.toString());
				lblScramblePath.setText(chooser.getSelectedFile().getName());
			}

		} else if (e.getSource() == btnChooseImage) {
			chooser = new JFileChooser();
			final FileNameExtensionFilter filter = new FileNameExtensionFilter(
					"JPG Images", "jpg");
			chooser.setFileFilter(filter);
			final int returnVal = chooser.showOpenDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				imageLoc = chooser.getSelectedFile().getAbsolutePath();
				lblImagePath.setText(chooser.getSelectedFile().getName());
			}
		}
	}

	public Puzzle getNewPuzzle() {
		return puzzle;
	}

	public boolean isConfirmed() {
		return confirmed;
	}
}
