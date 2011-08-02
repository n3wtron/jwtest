package net.alpha01.jwtest.beans;

import java.io.Serializable;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ProjectVersion implements Serializable{
	private static final long serialVersionUID = 8572153820925942465L;
	private BigInteger id_project;
	private String version;
	private String oldVersion;
	private Date released;
	
	public ProjectVersion(){
		
	}
	
	public ProjectVersion(BigInteger id_project, String version) {
		super();
		this.id_project = id_project;
		this.version = version;
	}
	public ProjectVersion(Project project) {
		setId_project(project.getId());
	}
	public BigInteger getId_project() {
		return id_project;
	}
	public void setId_project(BigInteger id_project) {
		this.id_project = id_project;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	
	public Date getReleased() {
		return released;
	}
	public void setReleased(Date released) {
		this.released = released;
	}
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ProjectVersion)){
			return false;
		}
		return ((ProjectVersion)(obj)).getId_project().equals(getId_project()) && ((ProjectVersion)(obj)).getVersion().equals(getVersion());
	}
	
	@Override
	public String toString() {
		SimpleDateFormat sdf = new SimpleDateFormat("-yyyy-MM-dd HH:mm");
		String strDate="";
		if (getReleased()!=null){
			strDate=sdf.format(getReleased());
		}
		return getVersion()+ strDate;
	}
	public String getOldVersion() {
		return oldVersion;
	}
	public void setOldVersion(String oldVersion) {
		this.oldVersion = oldVersion;
	}
}
