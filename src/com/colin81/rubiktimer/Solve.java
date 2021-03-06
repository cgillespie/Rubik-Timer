package com.colin81.rubiktimer;

public class Solve {

	private int id;
	private Profile profile;
	private String scramble;
	private long solveTime;
	private long dateTime;

	public Solve() {

	}

	public Solve(final int id) {
		this.id = id;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Solve)) {
			return false;
		}
		final Solve other = (Solve) obj;
		if (dateTime != other.dateTime) {
			return false;
		}
		if (id != other.id) {
			return false;
		}
		if (profile == null) {
			if (other.profile != null) {
				return false;
			}
		} else if (!profile.equals(other.profile)) {
			return false;
		}
		if (scramble == null) {
			if (other.scramble != null) {
				return false;
			}
		} else if (!scramble.equals(other.scramble)) {
			return false;
		}
		if (solveTime != other.solveTime) {
			return false;
		}
		return true;
	}

	/**
	 * 
	 * @return Returns the date that the solve was completed in the format of
	 *         milliseconds since epoch.
	 */
	public long getDateTime() {
		return dateTime;
	}

	public int getId() {
		return id;
	}

	/**
	 * 
	 * @return
	 * @see Profile
	 */
	public Profile getProfile() {
		return profile;
	}

	public String getScramble() {
		return scramble;
	}

	public long getSolveTime() {
		return solveTime;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (dateTime ^ (dateTime >>> 32));
		result = prime * result + id;
		result = prime * result + ((profile == null) ? 0 : profile.hashCode());
		result = prime * result
				+ ((scramble == null) ? 0 : scramble.hashCode());
		result = prime * result + (int) (solveTime ^ (solveTime >>> 32));
		return result;
	}

	public void setDateTime(final long dateTime) {
		this.dateTime = dateTime;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public void setProfile(final Profile profile) {
		this.profile = profile;
	}

	public void setScramble(final String scramble) {
		this.scramble = scramble;
	}

	public void setSolveTime(final long solveTime) {
		this.solveTime = solveTime;
	}

	@Override
	public String toString() {
		return "Solve [id=" + id + ", profile=" + profile + ", scramble="
				+ scramble + ", solveTime=" + solveTime + ", dateTime="
				+ dateTime + "]";
	}

}
