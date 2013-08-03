package com.colin81.rubiktimer.dialogs;

import javax.swing.Icon;

public class AboutInfo {

	private Icon image;
	private String appname, version, description, copyright, license;

	public AboutInfo() {
	}

	public String getAppname() {
		return appname;
	}

	public String getCopyright() {
		return copyright;
	}

	public String getDescription() {
		return description;
	}

	public Icon getImage() {
		return image;
	}

	public String getLicense() {
		return license;
	}

	public String getVersion() {
		return version;
	}

	public void setAppname(final String appname) {
		this.appname = appname;
	}

	public void setCopyright(final String copyright) {
		this.copyright = copyright;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public void setImage(final Icon image) {
		this.image = image;
	}

	public void setLicense(final String license) {
		this.license = license;
	}

	public void setVersion(final String version) {
		this.version = version;
	}

}
