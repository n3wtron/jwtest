package net.alpha01.jwtest.dao;

import java.math.BigInteger;
import java.util.List;

import net.alpha01.jwtest.beans.Role;
import net.alpha01.jwtest.beans.User;

public interface UserMapper {
	
	class GroupUserAssociation{
		private BigInteger idUser;
		private BigInteger idGroup;
		
		public GroupUserAssociation(BigInteger idUser, BigInteger idGroup) {
			super();
			this.idUser = idUser;
			this.idGroup = idGroup;
		}
		public BigInteger getIdGroup() {
			return idGroup;
		}
		public void setIdGroup(BigInteger idGroup) {
			this.idGroup = idGroup;
		}
		public BigInteger getIdUser() {
			return idUser;
		}
		public void setIdUser(BigInteger idUser) {
			this.idUser = idUser;
		}
	}
	
	public User get(User user);
	public Integer add(User user);
	public Integer update (User user);
	public Integer delete(User user);
	public List<Role> getRoles(User user);
	public Integer deassociateRoles(BigInteger idUser);
	public Integer associateRole(GroupUserAssociation association);
	public List<User> getAll();
	public User getById(BigInteger idUser);
	
	
}
