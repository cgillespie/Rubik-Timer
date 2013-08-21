package com.colin81.rubiktimer;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;

public class MetronomePane extends JPanel implements Runnable {

	private static final int MIN = 45;
	private static final int MAX = 200;
	private static final int DEFAULT = 75;

	private static final long serialVersionUID = -7931585796490165827L;

	private final JButton btnStart;
	private final JSlider slider;

	private Clip clip = null;
	private boolean ticking;
	private Thread soundPlayer;

	public MetronomePane() {
		final ClassLoader cl = this.getClass().getClassLoader();

		AudioInputStream stream;
		try {
			stream = AudioSystem.getAudioInputStream(cl
					.getResource("ticktock.wav"));
			final AudioFormat format = stream.getFormat();
			final DataLine.Info info = new DataLine.Info(Clip.class, format);
			clip = (Clip) AudioSystem.getLine(info);
		} catch (final UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		setLayout(new MigLayout("", "[grow]", "[][][]"));

		final JLabel lblMetronome = new JLabel("Metronome");
		lblMetronome.setFont(new Font("SansSerif", Font.BOLD, 16));
		lblMetronome.setHorizontalAlignment(SwingConstants.CENTER);
		add(lblMetronome, "cell 0 0,alignx center");

		final JLabel lblBPM = new JLabel("");

		slider = new JSlider();
		slider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(final ChangeEvent e) {
				lblBPM.setText(slider.getValue() + " bpm");
			}
		});
		slider.setSnapToTicks(true);
		slider.setPaintLabels(true);
		slider.setPaintTicks(true);
		slider.setValue(DEFAULT);
		slider.setMinimum(MIN);
		slider.setMaximum(MAX);
		slider.setMinorTickSpacing(5);
		slider.setMajorTickSpacing(20);
		add(slider, "cell 0 1,growx");

		lblBPM.setText(slider.getValue() + " bpm");
		add(lblBPM, "flowx,cell 0 2,alignx left");

		final Component horizontalGlue = Box.createHorizontalGlue();
		add(horizontalGlue, "cell 0 2,grow");

		btnStart = new JButton("Start");
		btnStart.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				if (ticking) {
					ticking = false;
					soundPlayer.stop();
					btnStart.setText("Start");
				} else {
					ticking = true;
					// Create new Thread so sound doesn't block UI
					soundPlayer = new Thread(MetronomePane.this);
					soundPlayer.start();
					btnStart.setText("Stop");
				}

			}

		});
		add(btnStart, "cell 0 2,alignx right");
	}

	@Override
	public void run() {
		final ClassLoader cl = this.getClass().getClassLoader();
		try {
			final AudioInputStream stream = AudioSystem.getAudioInputStream(cl
					.getResource("ticktock.wav"));
			final AudioFormat format = stream.getFormat();
			final DataLine.Info info = new DataLine.Info(Clip.class, format);
			clip = (Clip) AudioSystem.getLine(info);
			clip.open(stream);

			while (true) {
				clip.start();
				clip.setFramePosition(0);
				Thread.sleep(1000 * 60 / slider.getValue());
				// TODO calculate the delay value
			}
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}