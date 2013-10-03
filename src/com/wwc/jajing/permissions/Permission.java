package com.wwc.jajing.permissions;

import com.wwc.jajing.permissions.PermissionManager.Permissions;

/*
 * Represents a unique permission that may be attached to a Permissable
 */
public interface Permission{
	
	public Long getId();
	public Permissions getName();
	public String getDescription();

	
	
}
