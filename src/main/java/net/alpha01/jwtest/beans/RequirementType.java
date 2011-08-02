package net.alpha01.jwtest.beans;

import java.io.Serializable;

public class RequirementType extends IdBean implements Serializable{
	private static final long serialVersionUID = -3140935443486556122L;

	private String name;
	private String description;
	
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
	@Override
	public String toString() {
		return name;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof RequirementType)){
			return false;
		}else{
			return ((RequirementType)obj).getId().equals(getId());
		}
	}
}
