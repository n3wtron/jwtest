package net.alpha01.jwtest.dao;

import java.math.BigInteger;
import java.util.List;

import net.alpha01.jwtest.beans.Role;

public interface RoleMapper {
	public Role get(BigInteger idGroup);
	public Integer add(Role grp);
	public Integer update (Role grp);
	public Integer delete(Role grp);
	public List<Role> getAll();
}
