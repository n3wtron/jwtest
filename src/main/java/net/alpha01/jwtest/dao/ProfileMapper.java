package net.alpha01.jwtest.dao;

import java.math.BigInteger;
import java.util.List;

import net.alpha01.jwtest.beans.Profile;

public interface ProfileMapper {
	Profile get(BigInteger idProfile);
	List<Profile> getAllByProject(BigInteger idProject);
	Integer add(Profile profile);
	Integer delete(Profile profile);
	Integer update(Profile profile);
	List<Profile> getAll();
}
