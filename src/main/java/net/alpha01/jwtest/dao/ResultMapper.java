package net.alpha01.jwtest.dao;

import java.math.BigInteger;
import java.util.List;

import net.alpha01.jwtest.beans.Result;
import net.alpha01.jwtest.beans.ResultMantis;

public interface ResultMapper {
	public class ResultSort extends Result{
		private static final long serialVersionUID = 1L;
		private boolean asc=false;
		private String sortColumn;
		public ResultSort(){
			
		}
		public ResultSort(int id_session) {
			setId_session(BigInteger.valueOf(id_session));
		}
		public boolean getAsc() {
			return asc;
		}
		public void setAsc(boolean asc) {
			this.asc = asc;
		}
		public String getSortColumn() {
			return sortColumn;
		}
		public void setSortColumn(String sortColumn) {
			this.sortColumn = sortColumn;
		}
	}
	
	
	Result get(BigInteger bigInteger);
	List<Result> getAll(ResultSort res);
	Integer add(Result result);
	Integer delete(Result key);
	Integer update(Result result);
	List<Result> getAllByTestCase(BigInteger idTestCase);
	List<ResultMantis> getAllMantis(Result result);
	Integer addMantis(ResultMantis rm);
	Integer delMantis(ResultMantis rm);
	List<Result>getRecycles(int idParent);
}
