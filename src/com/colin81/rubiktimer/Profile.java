package com.colin81.rubiktimer;

public class Profile {

	private int id;
	private Puzzle puzzle;
	private String name;
	private String description;

	public Profile() {

	}

	public Profile(final int id) {
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
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Profile other = (Profile) obj;
		if (description == null) {
			if (other.description != null) {
				return false;
			}
		} else if (!description.equals(other.description)) {
			return false;
		}
		if (id != other.id) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (puzzle == null) {
			if (other.puzzle != null) {
				return false;
			}
		} else if (!puzzle.equals(other.puzzle)) {
			return false;
		}
		return true;
	}

	public String getDescription() {
		return description;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Puzzle getPuzzle() {
		return puzzle;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ (description == null ? 0 : description.hashCode());
		result = prime * result + id;
		result = prime * result + (name == null ? 0 : name.hashCode());
		result = prime * result + (puzzle == null ? 0 : puzzle.hashCode());
		return result;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setPuzzle(final Puzzle puzzle) {
		this.puzzle = puzzle;
	}

	@Override
	public String toString() {
		return "Profile [id=" + id + ", puzzle=" + puzzle + ", name=" + name
				+ ", description=" + description + "]";
	}

}
