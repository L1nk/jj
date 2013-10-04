package com.wwc.jajing.permissions;

import android.content.Context;

import com.orm.SugarRecord;
import com.wwc.jajing.domain.entity.Caller;
import com.wwc.jajing.domain.entity.CallerImpl;

/*
 * This is the class that will link the Callers To Permissions (MANY TO MANY)
 * 
 * Each caller can be attached numerous permissions with an associated Boolean Value
 * 
 * Explanation:
 * 	caller1 can have a permission attached to him and a boolean indicating weather he can perform a given permission
 */
public class CallerPermission extends SugarRecord {

	private Long id;
	private CallerImpl CALLER;
	private PermissionImpl PERMISSION;
	private int HAS_PERMISSION;
	
	public CallerPermission(Context context) {
		super(context);
	}
	
	public CallerPermission(Context context, Caller caller, Permission perm, Boolean hasPermission)
	{
		super(context);
		this.CALLER = (CallerImpl) caller;
		this.PERMISSION =  (PermissionImpl) perm;
		this.HAS_PERMISSION = (hasPermission)? 1: 0;


	}
	
	public CallerImpl getCaller()
	{
		return this.CALLER;
	}
	
	public PermissionImpl getPermission()
	{
		return this.PERMISSION;
	}
	
	public Boolean getHasPermission()
	{
		
		return (this.HAS_PERMISSION == 1)? true: false;
	}
	
	
	public void setHasPermission(Boolean hasPermission)
	{
		if(hasPermission){
			this.HAS_PERMISSION = 1;
		} else {
			this.HAS_PERMISSION = 0;

		}
	}
	
	
	
	
	
	
	
	

}
