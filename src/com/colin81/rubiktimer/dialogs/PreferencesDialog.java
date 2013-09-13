package com.colin81.rubiktimer.dialogs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JSpinner;

import net.miginfocom.swing.MigLayout;

public class PreferencesDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = -4855551823227671692L;

	private final JButton btnOk;
	private final JButton btnCancel;

	private final JSpinner spinnerInspectionTime;

	private final Preferences pref;

	public PreferencesDialog(final Preferences p) {
		pref = p;
		setModal(true);
		this.setSize(320, 240);
		setTitle("Preferences");
		getContentPane().setLayout(new MigLayout("", "[][grow]", "[][]"));

		final JLabel lblInspectionTime = new JLabel("Inspection Time");
		getContentPane().add(lblInspectionTime, "cell 0 0");

		spinnerInspectionTime = new JSpinner();
		spinnerInspectionTime.setValue(pref.getInspectionTime());
		getContentPane().add(spinnerInspectionTime, "cell 1 0,alignx left");

		btnOk = new JButton("OK");
		btnOk.addActionListener(this);
		getContentPane().add(btnOk, "flowx,cell 1 1,alignx right");

		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(this);
		getContentPane().add(btnCancel, "cell 1 1,alignx right");

	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		if (e.getSource() == btnOk) {
			pref.setInspectionTime((int) spinnerInspectionTime.getValue());
			pref.saveProperties();
			this.setVisible(false);
			this.dispose();
		} else if (e.getSource() == btnCancel) {
			this.setVisible(false);
			this.dispose();
		}

	}

}
