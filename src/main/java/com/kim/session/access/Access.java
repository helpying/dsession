package com.kim.session.access;

import java.util.Enumeration;

/**
 * @author kim 2014年9月3日
 */
public interface Access {

	public String id();

	public int interval();

	public int interval(int interval);

	public Enumeration<String> names();

	public Object get(String name);

	public <T> T get(String name, T def);

	public void set(String name, Object value);

	public void remove();

	public void remove(String name);
}
