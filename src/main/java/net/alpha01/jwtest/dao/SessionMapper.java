package net.alpha01.jwtest.dao;

import java.math.BigInteger;
import java.util.List;

import net.alpha01.jwtest.beans.Session;

public interface SessionMapper {
	Session get(BigInteger id);
	List<Session> getAllByProject(BigInteger bigInteger);
	List<Session> getAllOpenedByProject(int idProject);
	Integer add(Session session);
	Integer delete(Session key);
	List<Session> getAllByPlan(int idPlan);
	Integer update(Session currSession);
	
}
