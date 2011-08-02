package net.alpha01.jwtest.beans;

import java.io.Serializable;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.alpha01.jwtest.dao.SqlConnection;
import net.alpha01.jwtest.dao.SqlSessionMapper;
import net.alpha01.jwtest.dao.UserMapper;

public class Session extends IdBean implements Serializable {

	private static final long serialVersionUID = 2294554101480486803L;
	
	private BigInteger id_plan;
	private BigInteger id_user;
	private String version;
	private Date start_date;
	private Date end_date;
	
	public BigInteger getId_plan() {
		return id_plan;
	}
	public void setId_plan(BigInteger id_plan) {
		this.id_plan = id_plan;
	}
	public Date getStart_date() {
		return start_date;
	}
	public void setStart_date(Date start_date) {
		this.start_date = start_date;
	}
	public Date getEnd_date() {
		return end_date;
	}
	public void setEnd_date(Date end_date) {
		this.end_date = end_date;
	}
	
	public String getUser(){
		if (getId_user()!=null){
			SqlSessionMapper<UserMapper> sesMapper = SqlConnection.getSessionMapper(UserMapper.class);
			String username = sesMapper.getMapper().getById(getId_user()).getUsername();
			sesMapper.close();
			return username;
		}
		return "";
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Session)){
			return false;
		}
		return ((Session)obj).getId().equals(getId());
	}
	
	@Override
	public String toString() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
		String version=getVersion()==null ? "" : getVersion();
		if (!version.isEmpty()){
			version+="-";
		}
		return version+getUser()+":"+sdf.format(getStart_date())+(getEnd_date()==null ?"":"-"+sdf.format(getEnd_date()));
	}
	
	public boolean isOpened() {
		return end_date==null;
	}
	
	public BigInteger getId_user() {
		return id_user;
	}
	public void setId_user(BigInteger id_user) {
		this.id_user = id_user;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
}
