package com.kim.session.http;

import java.io.IOException;
import java.util.Enumeration;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * For IT Test
 * 
 * @author kim 2014年9月3日
 */
public class SessionServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public SessionServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Enumeration<String> params = request.getParameterNames();
		while (params.hasMoreElements()) {
			String name = params.nextElement();
			if (name.startsWith("set_")) {
				String key = name.replaceFirst("set_", "");
				request.getSession().setAttribute(key, request.getParameter(name));
			} else if (name.startsWith("remove_")) {
				String key = name.replaceFirst("remove_", "");
				request.getSession().removeAttribute(key);
			}
		}
		Enumeration<String> names = request.getSession().getAttributeNames();
		TreeMap<String, Object> result = new TreeMap<String, Object>();
		while (names.hasMoreElements()) {
			String name = names.nextElement();
			result.put(name, name.startsWith("sys_") ? request.getSession().getAttribute(name) != null : request.getSession().getAttribute(name) != null ? request.getSession().getAttribute(name).toString() : "");
		}
		response.getWriter().write(result.toString());
	}
}
