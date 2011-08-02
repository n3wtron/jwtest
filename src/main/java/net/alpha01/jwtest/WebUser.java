package net.alpha01.jwtest;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.HashSet;

import net.alpha01.jwtest.beans.User;

public class WebUser implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private BigInteger id;
	private String username;
	private String name;
	private String password;
	private String email;
	private HashSet<String> roles;
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public HashSet<String> getRoles() {
		return roles;
	}
	public void setRoles(HashSet<String> roles) {
		this.roles = roles;
	}
	public BigInteger getId() {
		return id;
	}
	public void setId(BigInteger id) {
		this.id = id;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof WebUser){
			return ((WebUser)obj).getId().equals(getId());
		}
		if (obj instanceof User){
			return ((User)obj).getId().equals(getId());
		}
		return false;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		if (getName()!=null && !getName().isEmpty()){
			return getUsername()+"("+getName()+")";
		}else{
			return getUsername();
		}
	}
	
}
