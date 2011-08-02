package net.alpha01.jwtest.dao;

import java.math.BigInteger;
import java.util.List;

import net.alpha01.jwtest.beans.ProjectVersion;

public interface ProjectVersionMapper {
	ProjectVersion get(ProjectVersion pv);
	List<ProjectVersion> getAll(BigInteger idProject);
	Integer add(ProjectVersion pv);
	Integer update(ProjectVersion pv);
	Integer delete(ProjectVersion pv);
}
