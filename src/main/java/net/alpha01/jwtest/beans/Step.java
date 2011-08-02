package net.alpha01.jwtest.beans;

import java.io.Serializable;
import java.math.BigInteger;

public class Step extends IdBean implements Serializable{
	private static final long serialVersionUID = -8955321887978338765L;
	private BigInteger id_testcase;
	private String description;
	private String expected_result;
	private String failed_result;
	private String sequence;
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}
	public String getSequence() {
		return sequence;
	}
	public void setId_testcase(BigInteger id_testcase) {
		this.id_testcase = id_testcase;
	}
	public BigInteger getId_testcase() {
		return id_testcase;
	}
	public void setExpected_result(String expected_result) {
		this.expected_result = expected_result;
	}
	public String getExpected_result() {
		return expected_result;
	}
	public void setFailed_result(String failed_result) {
		this.failed_result = failed_result;
	}
	public String getFailed_result() {
		return failed_result;
	}
	
	@Override
	public String toString() {
		return description.replaceAll("\\<[^>]*>","");
	}
	
}
