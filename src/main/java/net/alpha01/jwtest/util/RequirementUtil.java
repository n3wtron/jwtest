package net.alpha01.jwtest.util;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.alpha01.jwtest.beans.Project;
import net.alpha01.jwtest.beans.Requirement;
import net.alpha01.jwtest.beans.TestCase;
import net.alpha01.jwtest.dao.RequirementMapper;
import net.alpha01.jwtest.dao.SqlSessionMapper;
import net.alpha01.jwtest.dao.TestCaseMapper;
import net.alpha01.jwtest.dao.TestCaseMapper.TestCaseSelectSort;

import org.apache.log4j.Logger;

public class RequirementUtil {
	public static boolean copyRequirement(Requirement req, Project destPrj,SqlSessionMapper<?>sesMapper){
		RequirementMapper reqMapper=sesMapper.getSqlSession().getMapper(RequirementMapper.class);
		BigInteger curReqID=req.getId();
		req.setId_project(destPrj.getId());
		req.setId(null);
		//find new progressive num
		HashMap<String, Object> qryParams = new HashMap<String, Object>();
		qryParams.put("idType", req.getType().getId());
		qryParams.put("idProject", destPrj.getId());
		req.setNum(BigInteger.valueOf(reqMapper.nextNum(qryParams) != null ? reqMapper.nextNum(qryParams) : 1));
		if (!reqMapper.add(req).equals(1)){
			Logger.getLogger(RequirementUtil.class).error("SQL Error updating requirement ID:"+req.getId());
			return false;
		}else{
			//retrieve testcase
			boolean sqlOk=true;
			List<TestCase> reqTests = sesMapper.getSqlSession().getMapper(TestCaseMapper.class).getAll(new TestCaseSelectSort(curReqID,"name", true));
			if (reqTests!=null){
				Iterator<TestCase> itT=reqTests.iterator();
				while (itT.hasNext() && sqlOk){
					TestCase rt=itT.next();
					sqlOk&=TestCaseUtil.copyTestCase(rt, req, sesMapper);
				}
			}
			if (!sqlOk){
				Logger.getLogger(RequirementUtil.class).error("SQL Error copying testcases");
				return false;
			}else{
				return true;
			}
		}
	}
}
