package net.alpha01.jwtest.exceptions;

import org.apache.log4j.Logger;


public class JWTestException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public JWTestException(Class<?> cl, Exception e) {
		super(e);
		Logger.getLogger(cl).error(e.getMessage(),e);
	}

	public JWTestException(Class<?> cl , String message) {
		super(message);
		Logger.getLogger(cl).error(message);
	}
}
