package com.kim.session.access.impl;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.command.BinaryCommandFactory;
import net.rubyeye.xmemcached.impl.KetamaMemcachedSessionLocator;
import net.rubyeye.xmemcached.utils.AddrUtil;

import com.kim.session.access.Access;
import com.kim.session.access.AccessConfig;
import com.kim.session.access.AccessGenerator;

/**
 * @author kim 2014年9月3日
 */
public class XMemcachedAccessGenerator implements AccessGenerator {

	private final Set<String> names = new HashSet<String>();

	private MemcachedClient client;

	private int expire;

	@Override
	public Access generate(String key) {
		return new ProxyAccess(key);
	}

	@Override
	public AccessGenerator warm(AccessConfig config) throws Exception {
		XMemcachedClientBuilder builder = new XMemcachedClientBuilder(AddrUtil.getAddresses(config.config(AccessConfig.ADDRESSES)));
		builder.setCommandFactory(new BinaryCommandFactory());
		builder.setSessionLocator(new KetamaMemcachedSessionLocator());
		this.client = builder.build();
		this.expire = Integer.valueOf(config.config(AccessConfig.EXPIRE));
		return this;
	}

	private class ProxyAccess implements Access {

		private final String key;

		private final Set<String> names;

		public ProxyAccess(String key) {
			super();
			this.key = key;
			this.names = this.get(AccessConfig.NAMES, XMemcachedAccessGenerator.this.names);
			this.expire().create().config(AccessConfig.LAST, System.currentTimeMillis()).config(AccessConfig.EXPIRE, (System.currentTimeMillis() + XMemcachedAccessGenerator.this.expire * 1000));
		}

		private ProxyAccess create() {
			try {
				XMemcachedAccessGenerator.this.client.add(this.key(AccessConfig.CREATION), XMemcachedAccessGenerator.this.expire, System.currentTimeMillis());
				this.names.add(AccessConfig.CREATION);
				return this;
			} catch (Exception e) {
				throw new IOAccessException(e);
			}
		}

		private ProxyAccess expire() {
			if (System.currentTimeMillis() > this.get(AccessConfig.EXPIRE, 0L)) {
				this.remove();
			}
			return this;
		}

		private ProxyAccess config(String name, Object value) {
			this.set(name, value);
			return this;
		}

		private String key(String key) {
			return this.key + "_" + key;
		}

		@Override
		public String id() {
			return this.key;
		}

		@Override
		public int interval() {
			return XMemcachedAccessGenerator.this.expire;
		}

		/**
		 * Not Support
		 * 
		 * @param interval
		 * @return
		 */
		@Override
		public int interval(int interval) {
			return this.interval();
		}

		@Override
		public Enumeration<String> names() {
			final Iterator<String> names = this.names.iterator();
			return new Enumeration<String>() {

				@Override
				public boolean hasMoreElements() {
					return names.hasNext();
				}

				@Override
				public String nextElement() {
					return names.next();
				}
			};
		}

		@Override
		public Object get(String name) {
			return this.get(name, null);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T> T get(String name, T def) {
			try {
				Object value = XMemcachedAccessGenerator.this.client.get(this.key(name));
				return value != null ? (T) value : def;
			} catch (Exception e) {
				throw new IOAccessException(e);
			}
		}

		@Override
		public void set(String name, Object value) {
			try {
				XMemcachedAccessGenerator.this.client.set(this.key(name), XMemcachedAccessGenerator.this.expire, value);
				if (!this.names.contains(name)) {
					this.names.add(name);
					XMemcachedAccessGenerator.this.client.replace(this.key(AccessConfig.NAMES), XMemcachedAccessGenerator.this.expire, this.names);
				}
			} catch (Exception e) {
				throw new IOAccessException(e);
			}
		}

		private void remove(String name, boolean cascade) {
			try {
				XMemcachedAccessGenerator.this.client.delete(this.key(name));
				this.names.remove(name);
				if (cascade) {
					XMemcachedAccessGenerator.this.client.replace(this.key(AccessConfig.NAMES), XMemcachedAccessGenerator.this.expire, this.names);
				}
			} catch (Exception e) {
				throw new IOAccessException(e);
			}
		}

		@Override
		public void remove() {
			try {
				Enumeration<String> names = this.names();
				while (names.hasMoreElements()) {
					this.remove(names.nextElement(), false);
				}
				XMemcachedAccessGenerator.this.client.delete(this.key(AccessConfig.NAMES));
			} catch (Exception e) {
				throw new IOAccessException(e);
			}
		}

		@Override
		public void remove(String name) {
			this.remove(name, true);
		}
	}
}
