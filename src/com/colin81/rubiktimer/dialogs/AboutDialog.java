package com.colin81.rubiktimer.dialogs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

public class AboutDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = 6686400021336402851L;

	private final JButton btnClose;
	private final JButton btnLicense;

	public AboutDialog(final AboutInfo ai) {
		setLocationRelativeTo(null);
		setResizable(false);
		setSize(320, 240);
		setTitle("About " + ai.getAppname());

		final JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(new MigLayout("", "[grow]", "[][][][grow][][]"));

		final JLabel lblImage = new JLabel(ai.getImage());
		lblImage.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(lblImage, "cell 0 0,alignx center,aligny center");

		final JLabel lblAppname = new JLabel(ai.getAppname());
		lblAppname.setFont(new Font("DejaVu Sans", Font.BOLD, 14));
		panel.add(lblAppname, "cell 0 1,alignx center");

		final JLabel lblVersion = new JLabel(ai.getVersion());
		lblVersion.setFont(new Font("DejaVu Sans", Font.PLAIN, 12));
		lblVersion.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(lblVersion, "cell 0 2,alignx center,aligny center");

		final JLabel lblDescription = new JLabel(ai.getDescription());
		lblDescription.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(lblDescription, "cell 0 3,alignx center,aligny center");

		final JLabel lblCopyright = new JLabel(ai.getCopyright());
		lblCopyright.setFont(new Font("DejaVu Sans", Font.ITALIC, 10));
		lblCopyright.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(lblCopyright, "cell 0 4,alignx center,aligny center");

		btnLicense = new JButton("License");
		if (ai.getLicense() == null) {
			btnLicense.setEnabled(false);
		}
		btnLicense.addActionListener(this);
		panel.add(btnLicense, "flowx,cell 0 5");

		final Component horizontalGlue = Box.createHorizontalGlue();
		panel.add(horizontalGlue, "cell 0 5,growx");

		btnClose = new JButton("Close");
		btnClose.addActionListener(this);
		btnClose.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(btnClose, "cell 0 5");
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		if (e.getSource() == btnClose) {
			this.setVisible(false);
			this.dispose();
		} else if (e.getSource() == btnLicense) {
			// TODO: display license
		}

	}

}
