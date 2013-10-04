package com.wwc.jajing.system;

import com.wwc.jajing.system.JJSystemImpl.Services;

/*
 * This interface defines the contract for classes that provide a dictionary lookup for well known components in our system.
 * 
 * 
 */
public interface JJRegistry {
	
	public void addToRegistry(Object key, Object value);
	public Object getFromRegistry(Services key);
}
