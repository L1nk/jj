package com.wwc.jajing.permissions;

import android.content.Context;
import android.util.Log;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;
import com.wwc.jajing.permissions.PermissionManager.Permissions;

/*
 * Represents a Permission
 */
public class PermissionImpl extends SugarRecord implements Permission {

	private Long id;
	private String name;
	private String description;
	
	@Ignore
	private static final String TAG = "PermissionImpl";
	
	public PermissionImpl(Context context) {
		super(context);
	}

	public Long getId()
	{
		return id;
	}
	
	@Override
	public Permissions getName() {
		
		try{
			return Permissions.valueOf(this.name);

		}catch(Exception ex)
		{
			Log.d(TAG, "This permission have an invalid name.");
			ex.printStackTrace();
			return null;
		}
		
		
	}

	@Override
	public String getDescription() {
		return this.description;
	}

	/*
	 * This is used to load our permissions initially
	 * 
	 */
	public void setName(String aName) {
		this.name = aName;
	}

	public void setDescription(String aDescription) {
		this.description = aDescription;
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
