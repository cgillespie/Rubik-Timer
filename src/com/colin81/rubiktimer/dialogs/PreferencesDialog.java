package com.colin81.rubiktimer.dialogs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JSpinner;

import net.miginfocom.swing.MigLayout;

public class PreferencesDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = -4855551823227671692L;

	public PreferencesDialog() {
		setTitle("Preferences");
		getContentPane().setLayout(new MigLayout("", "[][grow]", "[][][]"));

		final JLabel lblInspectionTime = new JLabel("Inspection Time");
		getContentPane().add(lblInspectionTime, "cell 0 0");

		final JSpinner spinner = new JSpinner();
		getContentPane().add(spinner, "cell 1 0");

		final JLabel lblDefaultPuzzle = new JLabel("Default Puzzle");
		getContentPane().add(lblDefaultPuzzle, "cell 0 1,alignx trailing");

		final JComboBox comboBox = new JComboBox();
		getContentPane().add(comboBox, "cell 1 1,growx");

		final JButton btnOk = new JButton("OK");
		getContentPane().add(btnOk, "flowx,cell 1 2,alignx right");

		final JButton btnCancel = new JButton("Cancel");
		getContentPane().add(btnCancel, "cell 1 2,alignx right");

	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		// TODO Auto-generated method stub

	}

}
