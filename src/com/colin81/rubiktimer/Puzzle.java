package com.colin81.rubiktimer;

public class Puzzle {

	private int id;
	private String name;
	private String scrambler;
	private String image;

	public Puzzle() {

	}

	public Puzzle(final int id) {
		this.id = id;
	}

	public Puzzle(final String name) {
		this.name = name;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Puzzle)) {
			return false;
		}
		final Puzzle other = (Puzzle) obj;
		if (image == null) {
			if (other.image != null) {
				return false;
			}
		} else if (!image.equals(other.image)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (scrambler == null) {
			if (other.scrambler != null) {
				return false;
			}
		} else if (!scrambler.equals(other.scrambler)) {
			return false;
		}
		return true;
	}

	public int getId() {
		return id;
	}

	public String getImage() {
		return image;
	}

	public String getName() {
		return name;
	}

	public String getScrambler() {
		return scrambler;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (image == null ? 0 : image.hashCode());
		result = prime * result + (name == null ? 0 : name.hashCode());
		result = prime * result
				+ (scrambler == null ? 0 : scrambler.hashCode());
		return result;
	}

	public void setImage(final String image) {
		this.image = image;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setScrambler(final String scrambler) {
		this.scrambler = scrambler;
	}

	@Override
	public String toString() {
		return "Puzzle [id=" + id + ", name=" + name + ", scrambler="
				+ scrambler + ", image=" + image + "]";
	}
}
