package net.alpha01.jwtest.util;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.log4j.Logger;

public class JWTestConfig {
	private XMLConfiguration config;
	private static JWTestConfig instance;
	
	public JWTestConfig(){
		try {
			config=new XMLConfiguration(getConfigFileName());
			Logger.getLogger(getClass()).info("Loading config file:"+getConfigFileName());
			config.setDelimiterParsingDisabled(true); 
			config.setReloadingStrategy(new FileChangedReloadingStrategy());
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	private String getConfigFileName(){
		return System.getProperty("jwtest.config");
	}
	
	protected XMLConfiguration getConfig(){
		return config;
	}
	
	static public JWTestConfig getInstance(){
		if (instance==null){
			instance=new JWTestConfig();
		}
		return instance;
	}
	
	public static String getProp(String key){
		return getInstance().getConfig().getString(key);
	}
	
	public static String getProp(String key,String dfl){
		return getInstance().getConfig().getString(key,dfl);
	}
	
	public static boolean getPropAsBoolean(String key) {
		return getInstance().getConfig().getBoolean(key);
	}
	
	public synchronized static void setProp(String key, String value) throws ConfigurationException{
		getInstance().getConfig().setProperty(key, value);
		getInstance().getConfig().save();
	}
	
}
