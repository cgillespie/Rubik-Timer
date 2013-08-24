package com.colin81.rubiktimer.scramblers;

public interface Scrambler {

	public int getDefaultSize();

	public String getDescription();

	public String getNewScramble(int length);

}
