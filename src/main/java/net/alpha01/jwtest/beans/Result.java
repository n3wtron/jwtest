package net.alpha01.jwtest.beans;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

import net.alpha01.jwtest.dao.SessionMapper;
import net.alpha01.jwtest.dao.SqlConnection;
import net.alpha01.jwtest.dao.SqlSessionMapper;
import net.alpha01.jwtest.dao.TestCaseMapper;

public class Result extends IdBean implements Serializable {
	private static final long serialVersionUID = -6355451940678802670L;
	private BigInteger id_parent;
	private BigInteger id_session;
	private BigInteger id_testcase;
	private Boolean success;
	private Date insert_date;
	private String note;
	private BigInteger recycles;
	private Session session;
	private TestCase testCase;
	
	public BigInteger getId_session() {
		return id_session;
	}
	public void setId_session(BigInteger id_session) {
		this.id_session = id_session;
	}
	public BigInteger getId_testcase() {
		return id_testcase;
	}
	public void setId_testcase(BigInteger id_testcase) {
		this.id_testcase = id_testcase;
	}
	public Boolean getSuccess() {
		return success;
	}
	public void setSuccess(Boolean success) {
		this.success = success;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public Session getSession() {
		if (session==null){
			SqlSessionMapper<SessionMapper> ses=SqlConnection.getSessionMapper(SessionMapper.class);
			this.session=ses.getMapper().get(getId_session());
			ses.close();
		}
		return session;
	}
	public void setSession(Session session) {
		this.session = session;
	}
	
	public TestCase getTestCase() {
		if (testCase==null){
			SqlSessionMapper<TestCaseMapper> ses=SqlConnection.getSessionMapper(TestCaseMapper.class);
			this.testCase=ses.getMapper().get(getId_testcase());
			ses.close();
		}
		return testCase;
	}
	public void setTestCase(TestCase testCase) {
		this.testCase = testCase;
	}
	public BigInteger getId_parent() {
		return id_parent;
	}
	public void setId_parent(BigInteger id_parent) {
		this.id_parent = id_parent;
	}
	public Date getInsert_date() {
		return insert_date;
	}
	public void setInsert_date(Date insert_date) {
		this.insert_date = insert_date;
	}
	public BigInteger getRecycles() {
		return recycles;
	}
	public void setRecycles(BigInteger recycles) {
		this.recycles = recycles;
	}
	
}
