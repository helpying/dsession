package com.kim.session.http;

import junit.framework.TestCase;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

/**
 * @author kim 2014年9月4日
 */
public class SessionServletIT {

	@Test
	public void testNullUid() throws Exception {
		TestCase.assertEquals("", IOUtils.toString(Runtime.getRuntime().exec("curl http://127.0.0.1:8080").getInputStream()));
	}

	@Test
	public void testSingleUid() throws Exception {
		TestCase.assertEquals("{sys_creation=true, sys_expire=true, sys_last=true}", IOUtils.toString(Runtime.getRuntime().exec("curl http://127.0.0.1:8080?uid=KIM").getInputStream()));
	}

	@Test
	public void testSetParams() throws Exception {
		TestCase.assertEquals("{a=A, b=B, sys_creation=true, sys_expire=true, sys_last=true}", IOUtils.toString(Runtime.getRuntime().exec("curl http://127.0.0.1:8080?uid=KIM&set_a=A&set_b=B").getInputStream()));
	}

	@Test
	public void testRemoveParam() throws Exception {
		TestCase.assertEquals("{b=B, sys_creation=true, sys_expire=true, sys_last=true}", IOUtils.toString(Runtime.getRuntime().exec("curl http://127.0.0.1:8080?uid=KIM&remove_a=A").getInputStream()));
	}

	@Test
	public void testRemoveBeforeSetParam() throws Exception {
		TestCase.assertEquals("{c=C, sys_creation=true, sys_expire=true, sys_last=true}", IOUtils.toString(Runtime.getRuntime().exec("curl http://127.0.0.1:8080?uid=KIM&remove_b=B&set_c=C").getInputStream()));
	}

	@Test
	public void testSetBeforeRemoveParam() throws Exception {
		TestCase.assertEquals("{d=D, sys_creation=true, sys_expire=true, sys_last=true}", IOUtils.toString(Runtime.getRuntime().exec("curl http://127.0.0.1:8080?uid=KIM&set_d=D&remove_c=C").getInputStream()));
	}
}
