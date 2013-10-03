package com.wwc.jajing.domain.entity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;
import com.wwc.jajing.permissions.CallerPermission;
import com.wwc.jajing.permissions.Permission;
import com.wwc.jajing.permissions.PermissionManager.Permissions;


public class CallerImpl extends SugarRecord implements Caller{

	private Long id;
	private String number;
	private String name;
	private int hasApp = 0;
	
	@Ignore
	private HashMap<Permission, Boolean> permissions = null;
	@Ignore
	private static final String TAG = "CallerImpl";
	
	
	public CallerImpl(Context context) {
		super(context);
	
	}
	public CallerImpl(Context context, String name, String number)
	{
		super(context);
		this.name = name;
		this.number = number;

	}
	
	@Override
	public Long getId()
	{
		return this.id;
	}
	
	public boolean hasApp()
	{
		if(this.hasApp == 1)
			return true;
		else 
			return false;
	}
	
	public void setHasApp(Boolean hasApp)
	{
		if(hasApp == true) {
			this.hasApp = 1;
		} else {
			this.hasApp = 0;
		}
		this.save();
		Log.d(TAG, "Caller app status has been changed to:" + this.hasApp());
	}
	
	@Override
	public boolean isPermissionSet(Permissions aName) {
		
		if (aName == null) {
		 throw new IllegalArgumentException("Permission name cannot be null");
		}
		if (this.permissions == null) this.loadPermissions();
			
		for (Permission aPermission : this.permissions.keySet()) {
		   if(aPermission.getName().equals(aName)) return true;
		}
		return false;
	}
	

	@Override
	public boolean isPermissionSet(Permission perm) {
		
		if (perm == null) {
			 throw new IllegalArgumentException("Permission cannot be null");
		}
		if (this.permissions == null) this.loadPermissions();

		
		return isPermissionSet(perm.getName());
	}

	@Override
	public boolean hasPermission(Permissions aName) {
		if (this.permissions == null) this.loadPermissions();
		if(!this.isPermissionSet(aName)) throw new IllegalStateException("You cannot check if a caller has a permission, until the permission is set!");
		  
		Log.d(TAG, "Our permissions map is:" + this.permissions.toString());
		for (Permission aPermission : this.permissions.keySet()) {
			   if(aPermission.getName().equals(aName)) 
				   return this.permissions.get(aPermission);
		}
		return false;
	}

	@Override
	public boolean hasPermission(Permission perm) {
		if (this.permissions == null) this.loadPermissions();
		if(!this.isPermissionSet(perm)) throw new IllegalStateException("You cannot check if a caller has a permission, until the permission is set!");


		return this.permissions.get(perm);
	}

	@Override
	public String getNumber() {
		return this.number;
	}

	@Override
	public String getName() {
		return this.name;
	}

	/*
	 * Here we ask CallerPermission to provide us with the permissions for this calller.
	 */
	private void loadPermissions()
	{
		//Init our permissions
		this.permissions = new HashMap<Permission, Boolean>();
		List<CallerPermission> callerPermissions = this.getCallerPermissions();
		
		for(CallerPermission cp : callerPermissions){
			this.permissions.put(cp.getPermission(), cp.getHasPermission());

		}
		
		
		Log.d(TAG, "permissions have been loaded.");
		Log.d(TAG, "This caller has, " + callerPermissions.size()  + " permissions set.");
	}
	
	public void reloadPermissions()
	{
		this.loadPermissions();
	}
	
	/*
	 * Gets a list of the CallerPermissions this caller has
	 */
	private List<CallerPermission> getCallerPermissions()
	{
		
		List<CallerPermission> callerPermissions = CallerPermission.find(CallerPermission.class, "caller=?", new String[] {String.valueOf(this.id)});
		return callerPermissions;
	}

	
	public static boolean isCallerPersisted(String callersPhoneNumber)
	{
		String[] whereClauseParams = {callersPhoneNumber};
		List<CallerImpl> callerList = CallerImpl.find(CallerImpl.class, "number=?", whereClauseParams);
		Log.d(TAG, Arrays.toString(callerList.toArray()));
		Log.d(TAG, "List count:"+ callerList.size());
		if(callerList.size() > 0) {
			return true;
		}
		return false;
		
	}
	
	public static CallerImpl getCallerByPhoneNumber(String callersPhoneNumber) 
	{
		String[] whereClauseParams = {callersPhoneNumber};
		List<CallerImpl> callerList = CallerImpl.find(CallerImpl.class, "NUMBER=?", whereClauseParams);
		if(callerList.size() > 0) {
			return callerList.get(0);
		} else {
			Log.d(TAG, "There is no caller with that phone number:" + callersPhoneNumber);
			throw new NullPointerException("Caller can't be null!");
		}
	}
	
	
	
	
	
	
	

}
