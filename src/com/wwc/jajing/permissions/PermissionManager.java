package com.wwc.jajing.permissions;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.wwc.jajing.domain.entity.Caller;
import com.wwc.jajing.system.JJSystemImpl;
import com.wwc.jajing.system.JJSystemImpl.Services;

/*
 * Singleton
 * 
 * Responsible for loading the colleciton of Permissions available to our app.
 * 
 * Responsible for attaching Permissions to Permissables.
 */
public class PermissionManager {


	private static final String TAG = "PermissionManager";
	//Colleciton of Permissions
	private final EnumMap<Permissions, PermissionImpl> permissionCollection = new EnumMap<Permissions, PermissionImpl>(Permissions.class);
	
	public static final String SEND_SMS = "SEND_SMS";
	public static final String SEND_CALL = "SEND_CALL";
	public static final String LEAVE_VOICEMAIL = "LEAVE_VOICEMAIL";
	
	private static PermissionManager instance = null;
	private static Context context = (Context) JJSystemImpl.getInstance().getSystemService(Services.CONTEXT);
	
	//Singleton
	private PermissionManager(){
		
	}
	
	public static PermissionManager getInstance() {
		
		if(instance == null) {
			instance = new PermissionManager();
			instance.loadPermissions(context);
		}
		return instance;
	}

	

	
	//SET PERMISSIONS HERE. THESE PERMISSIONS GET LOADED ONCE IN THE DATABASE.
	public enum Permissions {
		SEND_SMS("SEND_SMS",
				"WEATHER A PERMISSIABLE CAN SEND A SMS THROUGH WHEN USER IS AWAY"), 
		SEND_CALL("SEND_CALL",
				"WEATHER A PERMISSABLE CAN SEND A CALL THROUGH WHEN USER IS AWAY"),
		LEAVE_VOICEMAIL("LEAVE_VOICEMAIL",
				"WEATHER A PERMISSABLE CAN SEND A VOICEMAIL THROUGH WHEN USER IS AWAY");

		private final String name;
		private final String description;

		private Permissions(String name, String description) {
			this.name = name;
			this.description = description;
		}

		public String getName()
		{
			return this.name;
		}
		public String getDescription() {
			return this.description;
		}

		public String toString() {
			return name;
		}

	};

	/*
	 * This initially loads the available permissions that we can assign to
	 * Permissables in our database, and stores each permission in a collection
	 * for later retrieval.
	 * 
	 */
	public final void loadPermissions(Context context) {
		this.context = context;
		// check if permissions defined in our enum and permissions in our database are in sync
		if (!isPermissionsSynced()){
			// if not, delete all permissions and load the new permissions.
			this.deleteAllPermissions();
			for (Permissions permission : Permissions.values()) {

				PermissionImpl perm = new PermissionImpl(context);
				perm.setName(permission.getName());
				perm.setDescription(permission.getDescription());
				Log.d(TAG, permission.getName());
				perm.save();
				
				// also add them to our pemrissionCollection for later retrieval
				Log.d(TAG, String.valueOf(permission.ordinal()));
				permissionCollection.put(permission, perm);
			}
		}
			//This line is to ensure that the Permission Objects get the Ids that were automatically assigned upon saving
			// There was a bug with the permission Ids not being set when the objects are first created.
			this.restorePermissions();
	
		
		
	}
	
	/*
	 * Check to make sure permissions loaded and permissions in enum are teh same size.
	 * If they are the same size, we can assume they are in SYNC
	 */
	private Boolean isPermissionsSynced()
	{
		List<PermissionImpl> permList = PermissionImpl.listAll(PermissionImpl.class);
		int permListSize = permList.size();		
		int enumPermissionsSize = Permissions.values().length;

		if(permListSize != enumPermissionsSize) {
			Log.d(TAG, "Permissions are NOT in SYNC");

			return false;
		}
		else {
			//check to make sure each permission loaded has a corresponding enum value
			for(PermissionImpl p : permList)
			{
				try{
					p.getName();
				}catch(Exception ex){
					Log.d(TAG, "Permissions are NOT in SYNC");
					ex.printStackTrace();

					return false;
				}
			}
			
			Log.d(TAG, "Permissions are in SYNC");

			return true;
		}
	}
	

	private void restorePermissions()
	{
		Log.d(TAG, "Permission being restored...");
		//clear our permissionCollection
		permissionCollection.clear();
		//iterate through our permissions
		List<PermissionImpl> permList = PermissionImpl.listAll(PermissionImpl.class);
		for(PermissionImpl perm : permList)
		{
			permissionCollection.put(Permissions.valueOf(perm.getName().toString()), perm);
		}
	}
	
	private void deleteAllPermissions()
	{
		List<PermissionImpl> permList = PermissionImpl.listAll(PermissionImpl.class);
		for(PermissionImpl perm : permList)
		{
			perm.delete();
		}
	}
	
	
	public Permission getPermission(Permissions aPermission)
	{
		if (aPermission == null) {
			throw new IllegalArgumentException("permission name cannot be null.");
		}
		if( permissionCollection.get(aPermission) == null) {
			throw new NullPointerException("the permission you are attempting to retrieve is null");
		}
		Log.d(TAG, "Getting permission ordinal:" + String.valueOf(aPermission.ordinal()));
		Permission perm = permissionCollection.get(aPermission);
		Log.d(TAG, "Getting permission id:" + perm.getId());
		Log.d(TAG, this.permissionCollection.toString());
		return perm;
	}
	
	
	/*
	 * Attach a  Permission to a Permissable
	 *
	 */
	
	public void attachPermission(Caller permissable, Permission perm, Boolean givePermission)
	{
		
		if(permissable.isPermissionSet(perm)) { //check if caller has the permission set
			//if they do change the permission
			Log.d(TAG, "Permission id to be attached:" + perm.getId().toString());
			String permissionId = perm.getId().toString();
			String callerId = permissable.getId().toString();
			
			List<CallerPermission> cpList = CallerPermission.find(CallerPermission.class, "CALLER=? and PERMISSION=?", new String[] {callerId, permissionId });
			Log.d(TAG, "Size of CallerPemrission List:" + cpList.size());
			CallerPermission cp = cpList.get(0);
			cp.setHasPermission(givePermission);
			cp.save();
			Log.d(TAG, "Finished Changing EXISTING Permissions");
			
		} else {//if permission is not set, set it...
			Log.d(TAG, "Permission not set, attaching permissions");
			CallerPermission cp = new CallerPermission(context, permissable, perm, givePermission);
			cp.save();
			Log.d(TAG, "Finished attaching NEW permissions to caller.");
			
			
		}
		
		//after permissions have been changed, ask the caller to reload his permissions
		permissable.reloadPermissions();
		
		
		
	}

	
	
	
	
	
	
	
	
	
	
	
	
	

}
