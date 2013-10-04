package com.wwc.jajing.domain.entity;

import com.wwc.jajing.permissions.Permissable;

public interface Caller extends Permissable, Entity {

	public String getNumber();
	public String getName();
	
	public void reloadPermissions();
	public void setHasApp(Boolean hasApp);
	public boolean hasApp();
}
