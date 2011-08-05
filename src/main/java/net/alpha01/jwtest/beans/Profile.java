package net.alpha01.jwtest.beans;

import java.math.BigInteger;

import net.alpha01.jwtest.dao.ProjectMapper;
import net.alpha01.jwtest.dao.SqlConnection;
import net.alpha01.jwtest.dao.SqlSessionMapper;

public class Profile extends IdBean{
	private static final long serialVersionUID = 1L;
	private BigInteger id_project;
	private String name;
	private String description;
	private String projectName;
	
	public Profile(BigInteger id_project, String name, String description) {
		super();
		this.id_project = id_project;
		this.name = name;
		this.description = description;
	}
	public Profile() {
		super();
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
	public BigInteger getId_project() {
		return id_project;
	}
	public void setId_project(BigInteger id_project) {
		this.id_project = id_project;
	}
	
	public String getProjectName() {
		if (this.projectName==null){
			if (getId_project()!=null){
				SqlSessionMapper<ProjectMapper> sesMapper = SqlConnection.getSessionMapper(ProjectMapper.class);
				Project prj = sesMapper.getMapper().get(getId_project().intValue());
				setProjectName(prj.getName());
				sesMapper.close();
			}else{
				this.projectName=null;
			}
		}
		return this.projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	
	@Override
	public String toString() {
		return  getName();
	}

}
