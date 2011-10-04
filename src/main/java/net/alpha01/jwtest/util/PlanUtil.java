package net.alpha01.jwtest.util;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import net.alpha01.jwtest.beans.Plan;
import net.alpha01.jwtest.beans.Session;
import net.alpha01.jwtest.beans.TestCase;
import net.alpha01.jwtest.dao.PlanMapper;
import net.alpha01.jwtest.dao.PlanMapper.FK;
import net.alpha01.jwtest.dao.SessionMapper;
import net.alpha01.jwtest.dao.SqlConnection;
import net.alpha01.jwtest.dao.SqlSessionMapper;
import net.alpha01.jwtest.exceptions.JWTestException;

import org.apache.log4j.Logger;

public class PlanUtil {

	public static boolean isUsed(Plan plan) {
		SqlSessionMapper<SessionMapper> sesMapper = SqlConnection.getSessionMapper(SessionMapper.class);
		List<Session> planSessions = sesMapper.getMapper().getAllByPlan(plan.getId().intValue());
		sesMapper.close();
		boolean allOpened=true;
		for (Session ses: planSessions){
			allOpened=allOpened && ses.isOpened();
		}
		return !allOpened;
	}

	public static void updatePlan(Plan plan, HashSet<TestCase> testCasesSet) throws JWTestException {
		SqlSessionMapper<PlanMapper> sesPlanMapper = SqlConnection.getSessionMapper(PlanMapper.class);
		if (isUsed(plan)) {
			// if there is any session that use this plan, will create e new
			// plan version
			Plan origPlan = sesPlanMapper.getMapper().get(plan.getId());
			if (sesPlanMapper.getMapper().add(plan).equals(1)) {
				origPlan.setNew_version(plan.getId());
				if (!sesPlanMapper.getMapper().update(origPlan).equals(1)) {
					sesPlanMapper.rollback();
					sesPlanMapper.close();
					throw new JWTestException(PlanUtil.class, "plan update Error");
				}

				Iterator<TestCase> itt = testCasesSet.iterator();
				while (itt.hasNext()) {
					TestCase test = itt.next();
					Logger.getLogger(PlanUtil.class).debug("adding testcase:" + test);
					if (!sesPlanMapper.getMapper().addTestCase(new FK(plan.getId(), test.getId())).equals(1)) {
						sesPlanMapper.rollback();
						sesPlanMapper.close();
						throw new JWTestException(PlanUtil.class, "Failed adding testcases association");
					}
				}
				sesPlanMapper.commit();
				sesPlanMapper.close();
			} else {
				sesPlanMapper.rollback();
				sesPlanMapper.close();
				throw new JWTestException(PlanUtil.class, "Failed updating plan");
			}
		} else {
			if (sesPlanMapper.getMapper().update(plan).equals(1)) {
				Integer testsDeleted = sesPlanMapper.getMapper().deleteTestCase(plan.getId());
				Logger.getLogger(PlanUtil.class).debug("TestCases association deleted:" + testsDeleted);

				Iterator<TestCase> itt = testCasesSet.iterator();
				while (itt.hasNext()) {
					TestCase test = itt.next();
					Logger.getLogger(PlanUtil.class).debug("adding testcase:" + test);
					if (!sesPlanMapper.getMapper().addTestCase(new FK(plan.getId(), test.getId())).equals(1)) {
						sesPlanMapper.rollback();
						sesPlanMapper.close();
						throw new JWTestException(PlanUtil.class, "Failed adding testcases association");
					}
				}
				sesPlanMapper.commit();
				sesPlanMapper.close();
			} else {
				sesPlanMapper.rollback();
				sesPlanMapper.close();
				throw new JWTestException(PlanUtil.class, "Failed updating plan");
			}
		}
	}

	public static void deletePlan(Plan plan) throws JWTestException {
		SqlSessionMapper<PlanMapper> mapper = SqlConnection.getSessionMapper(PlanMapper.class);
		if (isUsed(plan)) {
			plan.setNew_version(BigInteger.valueOf(-1));
			if (!mapper.getMapper().update(plan).equals(1)){
				mapper.rollback();
				mapper.close();
				throw new JWTestException(PlanUtil.class, "Cannot set -1 to new_version plan");
			}
			mapper.commit();
			mapper.close();
		} else {
			if (mapper.getMapper().delete(plan).equals(1)) {
				mapper.commit();
				mapper.close();
			} else {
				mapper.rollback();
				mapper.close();
				throw new JWTestException(PlanUtil.class, "ERROR: Plan not deleted SQL ERROR");
			}
		}
		JWTestUtil.cleanDeadElements();
	}
	
	public static void cleanPlan(){
		SqlSessionMapper<PlanMapper> mapper = SqlConnection.getSessionMapper(PlanMapper.class);
		mapper.getMapper().cleanPlan();
		mapper.commit();
		mapper.close();
	}
}
