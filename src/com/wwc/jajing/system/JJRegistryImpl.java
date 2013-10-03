package com.wwc.jajing.system;

import java.util.HashMap;

import com.wwc.jajing.system.JJSystemImpl.Services;

/*
 * This is our system registry. We can add global services to our system here.
 */
public class JJRegistryImpl implements JJRegistry{

	private static HashMap<Object, Object> registry;
	private static final JJRegistry instance =  new JJRegistryImpl();
	
	private JJRegistryImpl()
	{
		registry = new HashMap<Object, Object>();
	}
	
	static public JJRegistry getInstance()
	{

		return instance;
	}
	
	@Override
	public void addToRegistry(Object key, Object value) {
		
		registry.put(key, value);
		
	}

	@Override
	public Object getFromRegistry(Services key) {
		
		return registry.get(key);
	}

}
