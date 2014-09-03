package com.kim.session.http;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.kim.session.access.AccessConfig;
import com.kim.session.access.AccessGenerator;

/**
 * @author kim 2014年9月3日
 */
public class SessionFilter implements Filter {

	private final String param = "identify";

	private final String clazz = "class";

	private AccessGenerator generator;

	private String identify;

	public void init(FilterConfig config) throws ServletException {
		try {
			this.identify = config.getInitParameter(this.param);
			this.generator = AccessGenerator.class.cast(Class.forName(config.getInitParameter(this.clazz)).newInstance()).warm(new MemoryAccessConfig(config));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		String identify = request.getParameter(this.identify);
		if (identify == null) {
			return;
		}
		chain.doFilter(new SessionFilterRequest(this.generator.generate(identify), HttpServletRequest.class.cast(request)), response);
	}

	public void destroy() {
	}

	private final class MemoryAccessConfig implements AccessConfig {

		private final FilterConfig config;

		public MemoryAccessConfig(FilterConfig config) {
			super();
			this.config = config;
		}

		@Override
		public String config(String key) {
			return this.config.getInitParameter(key);
		}
	}
}
