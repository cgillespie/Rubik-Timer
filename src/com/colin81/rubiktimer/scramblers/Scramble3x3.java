package com.colin81.rubiktimer.scramblers;

import java.util.Random;

public class Scramble3x3 implements Scrambler {

	public static final int DEFAULT_LENGTH = 25;

	private static final String description = "Standard 3x3x3 scrambler.";

	public static void main(final String[] args) {
		final Scrambler scrm = new Scramble3x3();
		for (int i = 0; i < 10; i++) {
			System.out.println(scrm.getNewScramble(25));
		}
	}

	private final Random generator = new Random();
	private final String axisX[] = { "U", "D" };
	private final String axisY[] = { "L", "R" };
	private final String axisZ[] = { "F", "B" };

	private final String rot[] = { "", "\'", "2" };

	@Override
	public int getDefaultSize() {
		// TODO Auto-generated method stub
		return Scramble3x3.DEFAULT_LENGTH;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String getNewScramble(final int length) {
		String dualplate[] = new String[2];
		int currentmove, currentaxis, currentplate;
		int lastplate = -1, lastaxis = -1, last2axis = -1;

		String scramble = "";

		for (int i = 0; i < length; i++) {
			currentmove = generator.nextInt(3);
			currentaxis = generator.nextInt(3);
			currentplate = generator.nextInt(2);

			if (currentaxis == lastaxis) {
				if (lastaxis == last2axis) {
					currentaxis++;
					if (currentaxis == 3) {
						currentaxis = 0;
					}
				} else {
					currentplate = lastplate + 1;
					if (currentplate == 2) {
						currentplate = 0;
					}
				}
			}

			last2axis = lastaxis;
			lastaxis = currentaxis;
			lastplate = currentplate;

			switch (lastaxis) {
			case 0: {
				dualplate = axisX;
				break;
			}
			case 1: {
				dualplate = axisY;
				break;
			}
			case 2: {
				dualplate = axisZ;
				break;
			}
			}

			scramble += dualplate[currentplate] + rot[currentmove] + " ";
		}
		return scramble;
	}

}
