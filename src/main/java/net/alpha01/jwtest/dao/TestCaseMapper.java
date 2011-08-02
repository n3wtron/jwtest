package net.alpha01.jwtest.dao;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

import net.alpha01.jwtest.beans.TestCase;

public interface TestCaseMapper {
	
	class TestCaseSelectSort{
		private BigInteger id_requirement;
		private String sort;
		private boolean asc;
		
		
		public TestCaseSelectSort(BigInteger id_requirement, String sort, boolean asc) {
			super();
			this.id_requirement=id_requirement;
			this.sort = sort;
			this.asc = asc;
		}
	
		public String getSort() {
			return sort;
		}
		public void setSort(String sort) {
			this.sort = sort;
		}
		public boolean isAsc() {
			return asc;
		}
		public void setAsc(boolean asc) {
			this.asc = asc;
		}
		public void setId_requirement(BigInteger id_requirement) {
			this.id_requirement = id_requirement;
		}

		public BigInteger getId_requirement() {
			return id_requirement;
		}	
	}
	
	class Dependency implements Serializable{
		private static final long serialVersionUID = 1L;
		private BigInteger idTestCase;
		private BigInteger idDependency;
		
		public Dependency(BigInteger idTestCase, BigInteger idDependency) {
			super();
			this.idTestCase = idTestCase;
			this.idDependency = idDependency;
		}
		public BigInteger getIdTestCase() {
			return idTestCase;
		}
		public void setIdTestCase(BigInteger idTestCase) {
			this.idTestCase = idTestCase;
		}
		public BigInteger getIdDependency() {
			return idDependency;
		}
		public void setIdDependency(BigInteger idDependency) {
			this.idDependency = idDependency;
		}		
		
		
	}
	
	TestCase get(BigInteger id);
	TestCase getStat(BigInteger id);
	List<TestCase> getAll(TestCaseSelectSort sort);
	List<TestCase> getAllStat(TestCaseSelectSort sort);
	Integer add(TestCase test);
	Integer update(TestCase test);
	Integer delete(TestCase test);
	Integer addDependency(Dependency dep);
	List<TestCase> getDependencies(BigInteger id);
	Integer deleteDependencies(BigInteger id);
	List<TestCase> getAllByProject(BigInteger id);
	List<TestCase> getAllUncheckedBySession(BigInteger idSession);
	List<TestCase> getAllByPlan(BigInteger bigInteger);
}
