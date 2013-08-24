package com.colin81.rubiktimer.dialogs;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import com.colin81.rubiktimer.Profile;
import com.colin81.rubiktimer.Puzzle;

public class NewProfileDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = -8365883298009351653L;

	private final JTextField nameEntry;
	private final JButton btnOk;
	private final JButton btnCancel;
	private final JComboBox<String> comboBox;
	private final Map<String, Puzzle> puzzleNames;

	private Profile profile = null;
	private boolean confirmed;
	private final JTextField descriptionEntry;

	public NewProfileDialog(final List<Puzzle> puzzles, final Puzzle selected) {
		puzzleNames = new HashMap<String, Puzzle>();

		setMinimumSize(new Dimension(380, 175));
		this.setModal(true);
		setLocationRelativeTo(null);
		setTitle("Add New Profile");
		getContentPane().setLayout(new MigLayout("", "[][grow]", "[][][][][]"));

		final JLabel lblPuzzle = new JLabel("Puzzle");
		getContentPane().add(lblPuzzle, "cell 0 0,alignx trailing");

		comboBox = new JComboBox<String>();
		for (final Puzzle p : puzzles) {
			System.out.println(p.toString());
			comboBox.addItem(p.getName());
			puzzleNames.put(p.getName(), p);
		}
		comboBox.setSelectedItem(selected.getName());
		getContentPane().add(comboBox, "cell 1 0,growx");

		final JLabel lblProfileName = new JLabel("Profile Name");
		getContentPane().add(lblProfileName, "cell 0 1,alignx trailing");

		nameEntry = new JTextField();
		getContentPane().add(nameEntry, "cell 1 1,growx");
		nameEntry.setColumns(10);

		final JLabel lblDescription = new JLabel("Description");
		getContentPane().add(lblDescription, "cell 0 2,alignx trailing");

		descriptionEntry = new JTextField();
		getContentPane().add(descriptionEntry, "cell 1 2,growx");
		descriptionEntry.setColumns(10);

		final JSeparator separator = new JSeparator();
		getContentPane().add(separator, "cell 0 3 2 1,growx");

		btnOk = new JButton("OK");
		btnOk.setMnemonic(KeyEvent.VK_ENTER);
		btnOk.addActionListener(this);
		getContentPane().add(btnOk, "flowx,cell 1 4,alignx right");

		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(this);
		getContentPane().add(btnCancel, "cell 1 4");

		setVisible(true);
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		if (e.getSource().equals(btnOk)) {
			if (!nameEntry.getText().trim().equals("")
					&& !nameEntry.getText().trim()
							.equals("Please enter a name")) {
				profile = new Profile();
				profile.setName(nameEntry.getText());
				System.out.println(comboBox.getSelectedItem());
				profile.setPuzzle(puzzleNames.get(comboBox.getSelectedItem()));
				profile.setDescription(descriptionEntry.getText());

				confirmed = true;
				setVisible(false);
				dispose();

			} else {
				nameEntry.requestFocus();
				nameEntry.setText("Please enter a name");
				nameEntry.selectAll();
			}

		} else if (e.getSource().equals(btnCancel)) {
			setVisible(false);
			dispose();
		}
	}

	public Profile getNewProfile() {
		return profile;
	}

	public boolean isConfirmed() {
		return confirmed;
	}
}
