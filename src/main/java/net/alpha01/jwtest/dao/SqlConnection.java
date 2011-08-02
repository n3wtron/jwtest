package net.alpha01.jwtest.dao;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;

import net.alpha01.jwtest.util.JWTestConfig;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class SqlConnection {
	private static SqlConnection sqlFactory = null;
	private static SqlSessionFactory sessionFactory = null;
 
	private SqlConnection() {
 
	}
 
	private static SqlConnection getInstance() throws IOException {
		Reader reader = null;
		if(sqlFactory == null) {
			reader = new InputStreamReader(SqlConnection.class.getResourceAsStream("ibatis.xml"));
			Properties connectionProp = new Properties();
			connectionProp.put("db.host", JWTestConfig.getProp("db.host"));
			connectionProp.put("db.port", JWTestConfig.getProp("db.port"));
			connectionProp.put("db.name", JWTestConfig.getProp("db.name"));
			connectionProp.put("db.username", JWTestConfig.getProp("db.username"));
			connectionProp.put("db.password", JWTestConfig.getProp("db.password"));
			sqlFactory = new SqlConnection();
			SqlConnection.sessionFactory = new SqlSessionFactoryBuilder().build(reader,connectionProp);
		}
		return sqlFactory;
	}
 
	public static synchronized SqlSessionFactory getSessionFactory() throws IOException {
		if(sqlFactory == null) {
			getInstance();
		}
		return sessionFactory;
	}
 
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}
	
	public static <T> T getMapper(Class<T> cl){
		try {
			return SqlConnection.getSessionFactory().openSession().getMapper(cl);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static <T> SqlSessionMapper<T> getSessionMapper(Class<T> cl){
		try {
			SqlSessionMapper<T> result = new SqlSessionMapper<T>();
			result.setSqlSession(SqlConnection.getSessionFactory().openSession());
			result.setMapper(result.getSqlSession().getMapper(cl));
			return result;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}