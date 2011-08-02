package net.alpha01.jwtest.beans;

import java.math.BigInteger;

public class Attachment extends IdBean {
	private static final long serialVersionUID = 1L;
	private BigInteger id_project;
	private String name;
	private String description;
	private String extension;
	
	public Attachment(){
		
	}
	public Attachment(Project prj) {
		setId_project(prj.getId());
	}

	public BigInteger getId_project() {
		return id_project;
	}
	public void setId_project(BigInteger id_project) {
		this.id_project = id_project;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getExtension() {
		return extension;
	}
	public void setExtension(String extension) {
		this.extension = extension;
	}
	
	

}
