package com.colin81.rubiktimer.dialogs;

public class NewPuzzleInfo {

	private boolean confirmed = false;;
	private String name;
	private String imagePath;
	private String scramblerPath;

	public String getImagePath() {
		return imagePath;
	}

	public String getName() {
		return name;
	}

	public String getScramblerPath() {
		return scramblerPath;
	}

	public boolean isConfirmed() {
		return confirmed;
	}

	public void setConfirmed(final boolean confirmed) {
		this.confirmed = confirmed;
	}

	public void setImagePath(final String imagePath) {
		this.imagePath = imagePath;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setScramblerPath(final String scramblerPath) {
		this.scramblerPath = scramblerPath;
	}

	@Override
	public String toString() {
		return "NewPuzzleInfo [confirmed=" + confirmed + ", name=" + name
				+ ", imagePath=" + imagePath + ", scramblerPath="
				+ scramblerPath + "]";
	}
}
