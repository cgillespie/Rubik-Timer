package com.colin81.rubiktimer;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import net.miginfocom.swing.MigLayout;

public class SolveRenderer extends JPanel implements ListCellRenderer<Solve> {

	private static Logger LOGGER = Logger.getLogger(SolveRenderer.class
			.getName());

	private static final long serialVersionUID = -3896168792604334663L;

	final JLabel lblSolvetime;

	public SolveRenderer() {
		setLayout(new MigLayout("", "[][grow][]", "[]"));
		lblSolvetime = new JLabel("empty");
		add(lblSolvetime, "cell 0 0,alignx left,aligny top");

		final JButton btnDelete = new JButton();
		btnDelete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				LOGGER.info("Someone requested to delete a time");
			}
		});
		btnDelete.setBackground(UIManager.getColor("Menu.background"));
		btnDelete.setHorizontalAlignment(SwingConstants.RIGHT);
		btnDelete.setBorder(null);
		btnDelete.setIcon(new ImageIcon(SolveRenderer.class
				.getResource("/images/delete_obj.gif")));
		add(btnDelete, "cell 2 0,alignx left,aligny top");
	}

	@Override
	public Component getListCellRendererComponent(
			final JList<? extends Solve> list, final Solve value,
			final int index, final boolean isSelected,
			final boolean cellHasFocus) {
		if (isSelected) {
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		} else {
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}

		lblSolvetime.setText(Utils.milliFormat(value.getSolveTime()));
		return this;
	}

}
