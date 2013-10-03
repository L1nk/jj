package com.wwc.jajing.permissions;

import com.wwc.jajing.domain.entity.Entity;
import com.wwc.jajing.permissions.PermissionManager.Permissions;

/*
 * Represents and object that may be assigned permissions
 */

public interface Permissable extends Entity{

	public boolean isPermissionSet(Permissions name);

	public boolean isPermissionSet(Permission perm);

	public boolean hasPermission(Permissions aPermission);

	public boolean hasPermission(Permission perm);
	

}
