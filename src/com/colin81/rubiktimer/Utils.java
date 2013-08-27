package com.colin81.rubiktimer;

import java.util.List;

public class Utils {

	/**
	 * Averages the solve times from a list of Solve objects.
	 * 
	 * @param solves
	 *            The list of solves to be averaged. The passed list needs to be
	 *            of type List&ltSolve&gt
	 * @param number
	 *            How many of the solves in the list should be averaged.
	 * @return The average solve time for the number of solves specified from
	 *         the list. Passing a value of 0 will average the entire list.<br>
	 *         <b>Example:</b> averageSolveTime(solves, 5); will average the 5
	 *         first solves.
	 * @see Solve
	 */
	public static long averageSolveTime(final List<Object> solves, int number) {
		if (number == 0 || number > solves.size()) {
			number = solves.size();
		}

		long total = 0;
		for (int i = 0; i < number; i++) {
			total += ((Solve) solves.get(i)).getSolveTime();
		}

		return total / number;
	}

	/**
	 * Formats a millisecond time to mm:ss:hh, minute, second, hundredth.
	 * 
	 * @param time
	 *            The length of time in milliseconds.
	 * @return formattedTime The time in the format MM:SS:ss
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
	 * @return The qualified class name.
	 */
	public static String pathToQualifiedName(final String path) {
		System.out.println("Qualifying path: " + path);
		String delim = System.getProperty("file.separator");
		// if using a windows style separator add the escape character
		if (delim.equals("\\")) {
			delim += "\\";
		}
		System.out.println("Detected path delimeter: " + delim);
		String name = path.substring(0, path.lastIndexOf('.'));
		name = name.replaceAll(delim, ".");
		System.out.println("Qualified path: " + name);
		return name;
	}
}