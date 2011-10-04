package net.alpha01.jwtest.dao;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

import net.alpha01.jwtest.beans.Plan;

public interface PlanMapper {
	class FK implements Serializable{
		private static final long serialVersionUID = -5444698202204868832L;
		private BigInteger id_plan;
		private BigInteger id_testcase;
		
		public FK(BigInteger id_plan, BigInteger id_testcase) {
			super();
			this.id_plan = id_plan;
			this.id_testcase = id_testcase;
		}
		public BigInteger getId_plan() {
			return id_plan;
		}
		public void setId_plan(BigInteger id_plan) {
			this.id_plan = id_plan;
		}
		public BigInteger getId_testcase() {
			return id_testcase;
		}
		public void setId_testcase(BigInteger id_testcase) {
			this.id_testcase = id_testcase;
		}
		
	}
	
	Plan get(BigInteger bigInteger);
	List<Plan> getAll(int idProject);
	List<Plan> getAllStat(int idProject);
	Integer add(Plan step);
	Integer delete(Plan key);
	Integer update(Plan step);
	
	Integer addTestCase(FK fk);
	Integer deleteTestCase(BigInteger idPlan);
	Integer cleanPlan();
}
