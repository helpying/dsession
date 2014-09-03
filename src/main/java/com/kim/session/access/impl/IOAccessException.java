package com.kim.session.access.impl;

/**
 * @author kim 2014年9月3日
 */
public class IOAccessException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public IOAccessException(Exception e) {
		super(e);
	}
}
