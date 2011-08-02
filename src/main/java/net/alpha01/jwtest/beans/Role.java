package net.alpha01.jwtest.beans;

import java.io.Serializable;

public class Role extends IdBean implements Serializable{
	private static final long serialVersionUID = 1L;
	private String name;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
}
