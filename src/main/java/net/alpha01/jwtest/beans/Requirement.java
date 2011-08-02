package net.alpha01.jwtest.beans;

import java.math.BigInteger;

import net.alpha01.jwtest.dao.RequirementMapper;
import net.alpha01.jwtest.dao.SqlConnection;
import net.alpha01.jwtest.dao.SqlSessionMapper;

public class Requirement  extends IdBean{
	private static final long serialVersionUID = 6204406130627880994L;
	private BigInteger oldId;
	private BigInteger id_project;
	private BigInteger id_type;
	private BigInteger num;
	private String name;
	private String description;
	//join to table requirementstype
	private RequirementType type;
	
	private BigInteger ntest;
	
	public Requirement(Integer idReq, Integer typeId) {
		setId(BigInteger.valueOf(idReq));
		this.id_type=BigInteger.valueOf(typeId);
	}
	public Requirement() {
		
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
	
	public void setNtest(BigInteger ntest) {
		this.ntest = ntest;
	}
	public BigInteger getNtest() {
		return ntest;
	}
	
	
	@Override
	public String toString() {
		return name;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj==null || getId()==null){
			return false;
		}
		if (!(obj instanceof Requirement) ){
			return false;
		}else{
			return ((Requirement)obj).getId().equals(getId());
		}
	}
	public void setId_project(BigInteger id_project) {
		this.id_project = id_project;
	}
	public BigInteger getId_project() {
		return id_project;
	}
	public void setId_type(BigInteger id_type) {
		this.id_type = id_type;
	}
	public BigInteger getId_type() {
		return id_type;
	}
	public void setOldId(BigInteger oldId) {
		this.oldId = oldId;
	}
	public BigInteger getOldId() {
		return oldId;
	}
	public void setNum(BigInteger num) {
		this.num = num;
	}
	public BigInteger getNum() {
		return num;
	}
	
	public RequirementType getType() {
		if (this.type!=null){
			return this.type;
		}
		SqlSessionMapper<RequirementMapper> sesMapper= SqlConnection.getSessionMapper(RequirementMapper.class);
		RequirementType type = sesMapper.getMapper().getType(getId_type());
		sesMapper.close();
		setType(type);
		return type;
	}
	
	public void setType(RequirementType type) {
		this.type = type;
		if (type!=null){
			setId_type(type.getId());
		}
	}
	
	
	
}
