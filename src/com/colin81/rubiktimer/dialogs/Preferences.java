package com.colin81.rubiktimer.dialogs;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

/**
 * Stores and retrieves a properties file containing preferences for Rubik Timer
 * 
 * @author Colin Gillespie
 * 
 */
public class Preferences {

	private static final String PROP_INSPECTION_TIME = "InspectionTime";

	private final String propertiesFile;
	private int inspectionTime;

	/**
	 * Creates a new instance and loads properties from the passed path if the
	 * file exists
	 * 
	 * @param file
	 *            The file path to be used for the properties file.
	 */
	public Preferences(final String file) {
		propertiesFile = file;
		loadProperties();
	}

	public int getInspectionTime() {
		return inspectionTime;
	}

	/**
	 * Load properties from file.
	 */
	private void loadProperties() {
		final Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(propertiesFile));
			inspectionTime = Integer.valueOf(properties
					.getProperty(PROP_INSPECTION_TIME));
		} catch (final IOException e) {

		}
	}

	/**
	 * Save properties to file.
	 */
	public void saveProperties() {
		try {
			final Properties props = new Properties();

			props.setProperty("InspectionTime", String.valueOf(inspectionTime));
			final OutputStream out = new FileOutputStream(propertiesFile);
			props.store(out, "Delete this file to restore defaults.");
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param time
	 *            Inspection time in seconds
	 */
	public void setInspectionTime(final int time) {
		inspectionTime = time;
	}

}
