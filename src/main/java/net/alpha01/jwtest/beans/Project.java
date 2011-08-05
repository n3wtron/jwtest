package net.alpha01.jwtest.beans;

import java.io.Serializable;

public class Project extends IdBean implements Serializable{

	private static final long serialVersionUID = 2206581153138038215L;
	private String name;
	private String description;
	private String mantis_url;
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMantis_url() {
		return mantis_url;
	}
	public void setMantis_url(String mantisURL) {
		this.mantis_url = mantisURL;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	public String getDescription() {
		return description;
	}
}
