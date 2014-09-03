package com.kim.session.http;

import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

import com.kim.session.access.Access;
import com.kim.session.access.AccessConfig;

/**
 * @author kim 2014年9月3日
 */
@SuppressWarnings("deprecation")
public class SessionFilterRequest extends HttpServletRequestWrapper {

	private final Access access;

	public SessionFilterRequest(Access access, HttpServletRequest request) {
		super(request);
		this.access = access;
	}

	public HttpSession getSession(boolean created) {
		return this.getSession();
	}

	public HttpSession getSession() {

		return new HttpSession() {

			@Override
			public String getId() {
				return SessionFilterRequest.this.access.id();
			}

			@Override
			public Enumeration<String> getAttributeNames() {
				return SessionFilterRequest.this.access.names();
			}

			@Override
			@Deprecated
			public String[] getValueNames() {
				throw NotSupportException.INSTANCE;
			}

			@Override
			public void setAttribute(String name, Object value) {
				SessionFilterRequest.this.access.set(name, value);
			}

			@Override
			public Object getAttribute(String name) {
				return SessionFilterRequest.this.access.get(name);
			}

			@Override
			public void putValue(String name, Object value) {
				SessionFilterRequest.this.access.set(name, value);
			}

			@Override
			public void removeAttribute(String name) {
				SessionFilterRequest.this.access.remove(name);
			}

			@Override
			public void removeValue(String name) {
				SessionFilterRequest.this.access.remove(name);
			}

			@Override
			public Object getValue(String name) {
				return SessionFilterRequest.this.access.get(name);
			}

			@Override
			public long getCreationTime() {
				return SessionFilterRequest.this.access.get(AccessConfig.CREATION, 0L);
			}

			@Override
			public long getLastAccessedTime() {
				return SessionFilterRequest.this.access.get(AccessConfig.LAST, 0L);
			}

			@Override
			public void setMaxInactiveInterval(int interval) {
				SessionFilterRequest.this.access.interval(interval);
			}

			@Override
			public int getMaxInactiveInterval() {
				return SessionFilterRequest.this.access.interval();
			}

			@Override
			public ServletContext getServletContext() {
				return SessionFilterRequest.super.getServletContext();
			}

			@Override
			public HttpSessionContext getSessionContext() {
				throw NotSupportException.INSTANCE;
			}

			@Override
			public void invalidate() {
				SessionFilterRequest.this.access.remove();
			}

			@Override
			public boolean isNew() {
				return false;
			}
		};
	}

	private static class NotSupportException extends RuntimeException {

		private static final long serialVersionUID = 1L;

		public final static RuntimeException INSTANCE = new NotSupportException();

		private NotSupportException() {
		}
	}
}
