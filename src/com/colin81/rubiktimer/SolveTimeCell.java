package com.colin81.rubiktimer;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

/**
 * This is a JPanel that acts as an element for a JList like component. It is
 * aimed for displaying a Solve in a custom list container.
 * 
 * @author Colin Gillespie
 * @see Solve
 * 
 */
public class SolveTimeCell extends JPanel {

	private static final long serialVersionUID = -4416790661471021303L;

	public SolveTimeCell(final Solve s, final StorageInterface si) {
		setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		final JLabel time = new JLabel(Utils.milliFormat(s.getSolveTime()));
		add(time);

		final Date d = new Date(s.getDateTime());
		final SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.applyPattern(sdf.toLocalizedPattern());
		setToolTipText(sdf.format(d));

		final JButton delete = new JButton("");
		// Deleting a solve leads to an interesting tab order that causes timer
		// action to delete further solves
		delete.setFocusable(false);

		delete.setBorder(null);
		delete.setIcon(new ImageIcon(SolveTimeCell.class
				.getResource("/images/delete_obj.gif")));
		delete.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				try {
					si.removeSolve(s);
					// Doing this automatically refreshes the statistics via a
					// container listener in the parent component
					SolveTimeCell.this.getParent().remove(SolveTimeCell.this);
					// TODO Just make SolveTimeCell implement ActionListener ?

				} catch (final Exception e1) {
					// TODO An interesting place to be handling the error?
					e1.printStackTrace();
				}
			}
		});

		final Component horizontalGlue = Box.createHorizontalGlue();
		add(horizontalGlue);
		add(delete);
	}
}
