package com.kim.session.access;

/**
 * @author kim 2014年9月3日
 */
public interface AccessGenerator {

	public Access generate(String key);

	public AccessGenerator warm(AccessConfig config) throws Exception;
}
