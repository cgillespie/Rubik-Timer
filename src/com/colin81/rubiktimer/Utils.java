package com.colin81.rubiktimer;

public class Utils {

	/**
	 * Formats a millisecond time to mm:ss:hh, minute, second, hundredth.
	 * 
	 * @param time
	 * @return formattedTime
	 */
	public static String milliFormat(final long time) {
		final int millis = (int) time / 10 % 100;
		final int seconds = (int) time / 1000 % 60;
		final int minutes = (int) time / 60000 % 60;
		final String formattedTime = String.format("%02d:%02d:%02d", minutes,
				seconds, millis);
		return formattedTime;
	}

	/**
	 * Formats a file path to a class into a fully qualified class name.
	 * 
	 * @param path
	 *            The relative path to the .class file
	 * @return
	 */
	public static String pathToQualifiedName(final String path) {
		final String delim = System.getProperty("file.separator");
		String name = path.substring(0, path.lastIndexOf('.'));
		name = name.replaceAll(delim, ".");
		return name;
	}
}
