package com.kim.session.access;

/**
 * @author kim 2014年9月3日
 */
public interface AccessConfig {

	public final static String ADDRESSES = "sys_addresses";

	public final static String CREATION = "sys_creation";

	public final static String EXPIRE = "sys_expire";

	public final static String NAMES = "sys_names";

	public final static String LAST = "sys_last";

	public String config(String key);
}
