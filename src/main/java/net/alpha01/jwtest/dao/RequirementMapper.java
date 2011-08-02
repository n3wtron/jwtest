package net.alpha01.jwtest.dao;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;

import net.alpha01.jwtest.beans.Requirement;
import net.alpha01.jwtest.beans.RequirementType;

import org.apache.ibatis.exceptions.PersistenceException;

public interface RequirementMapper {
	
	class RequirementSelectSort extends Requirement{
		private static final long serialVersionUID = 1L;
		private String sort;
		private boolean asc;
		
		public RequirementSelectSort(BigInteger idProject,String sort, boolean asc){
			setId_project(idProject);
			setSort(sort);
			setAsc(asc);
		}
		
		public RequirementSelectSort(BigInteger idProject, BigInteger idType, String sort, boolean asc) {
			setId_project(idProject);
			setId_type(idType);
			setSort(sort);
			setAsc(asc);
		}

		public String getSort() {
			return sort;
		}
		public void setSort(String sort) {
			this.sort = sort;
		}
		public boolean isAsc() {
			return asc;
		}
		public void setAsc(boolean asc) {
			this.asc = asc;
		}
	}
	
	class Dependency implements Serializable{
		private static final long serialVersionUID = 8550490802980295785L;
		private BigInteger idRequirement;
		private BigInteger idDependency;		
		
		public Dependency(BigInteger idRequirement, BigInteger idDependency) {
			super();
			this.idRequirement = idRequirement;
			this.idDependency = idDependency;
		}
		public void setIdRequirement(BigInteger idRequirement) {
			this.idRequirement = idRequirement;
		}
		public BigInteger getIdRequirement() {
			return idRequirement;
		}
		public void setIdDependency(BigInteger idDependency) {
			this.idDependency = idDependency;
		}
		public BigInteger getIdDependency() {
			return idDependency;
		}
		public void setRequirement(Requirement requirement) {
			this.idRequirement = requirement.getId();
		}
		public void setDependency(Requirement dependency) {
			this.idDependency = dependency.getId();
		}
	}
	
	Requirement get(BigInteger id);
	List<Requirement> getAll(RequirementSelectSort sort);
	List<RequirementType> getTypes();
	Integer add(Requirement req)throws PersistenceException;
	Integer delete(Requirement req);
	Integer addDependency(Dependency dep);
	Integer update(Requirement req);
	Integer deleteDependencies(BigInteger idReq);
	RequirementType getType(BigInteger idType);
	List<Requirement> getDependencies(BigInteger id);
	Integer nextNum(HashMap<String, Object> qryParams);
}
