package net.alpha01.jwtest.dao;

import java.io.Serializable;

import org.apache.ibatis.session.SqlSession;

public class SqlSessionMapper<T> implements Serializable{
	private static final long serialVersionUID = 1L;
	private T mapper;
	private SqlSession sqlSession;
	public T getMapper() {
		return mapper;
	}
	public void setMapper(T mapper) {
		this.mapper = mapper;
	}
	public SqlSession getSqlSession() {
		return sqlSession;
	}
	public void setSqlSession(SqlSession sqlSession) {
		this.sqlSession = sqlSession;
	}
	public void commit() {
		getSqlSession().commit();
	}
	public void rollback(){
		getSqlSession().rollback();
	}
	public void close(){
		getSqlSession().close();
	}
	
}
