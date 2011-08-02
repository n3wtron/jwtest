package net.alpha01.jwtest.beans;

import java.io.Serializable;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Plan extends IdBean implements Serializable{
	private static final long serialVersionUID = -3960064974354390005L;
	private BigInteger id_project;
	private BigInteger nSessions;
	private BigInteger nTests;
	private Date creation_date;
	private String name;
	
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
	public void setnSessions(BigInteger nSessions) {
		this.nSessions = nSessions;
	}
	public BigInteger getnSessions() {
		return nSessions;
	}
	public void setnTests(BigInteger nTests) {
		this.nTests = nTests;
	}
	public BigInteger getnTests() {
		return nTests;
	}
	public void setCreation_date(Date creation_date) {
		this.creation_date = creation_date;
	}
	public Date getCreation_date() {
		return creation_date;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Plan)){
			return false;
		}
		return ((Plan)obj).getId().equals(getId());
	}
	
	@Override
	public String toString() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return getName()+"("+sdf.format(getCreation_date())+")";
	}
	
	
}
