package com.colin81.rubiktimer.dialogs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

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

	private final JTextField textField;
	private final JButton btnOk;
	private final JButton btnCancel;
	private JComboBox<String> comboBox;

	private String name;
	private final Profile profile = null;
	private boolean confirmed;

	public NewProfileDialog(List<Puzzle> puzzles) {
		setModal(true);
		setTitle("Add New Profile");
		getContentPane().setLayout(new MigLayout("", "[][grow]", "[][][][]"));

		JLabel lblPuzzle = new JLabel("Puzzle");
		getContentPane().add(lblPuzzle, "cell 0 0,alignx trailing");

		JComboBox<String> comboBox = new JComboBox<String>();
		getContentPane().add(comboBox, "cell 1 0,growx");

		JLabel lblProfileName = new JLabel("Profile Name");
		getContentPane().add(lblProfileName, "cell 0 1,alignx trailing");

		textField = new JTextField();
		getContentPane().add(textField, "cell 1 1,growx");
		textField.setColumns(10);

		JSeparator separator = new JSeparator();
		getContentPane().add(separator, "cell 0 2 2 1,growx");

		btnOk = new JButton("OK");
		btnOk.addActionListener(this);
		getContentPane().add(btnOk, "flowx,cell 1 3,alignx right");

		btnCancel = new JButton("Cancel");
		btnOk.addActionListener(this);
		getContentPane().add(btnCancel, "cell 1 3");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(btnOk)) {

		} else if (e.getSource().equals(btnCancel)) {

		}
	}

	public Profile getNewProfile() {
		return profile;
	}

	public boolean isConfirmed() {
		return confirmed;
	}
}
