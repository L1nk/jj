package com.wwc.jajing.domain.entity;

import com.wwc.jajing.domain.value.AvailabilityTime;
import com.wwc.jajing.domain.value.PhoneNumber;
import com.wwc.jajing.domain.value.UserStatus;
import com.wwc.jajing.permissions.PermissionManager.Permissions;


/*
 * Represents the User
 * 
 */
public interface User extends Entity {
	
	public void makeCall(PhoneNumber phoneNumber);
	public void goUnavailable(String aReason, AvailabilityTime anAvailabilityTime); //convenience method, sets start time as now
	public boolean goUnavailable(String aReason, String aStartTime, AvailabilityTime anAvailabilityTime);//this method specifies a start and end time
	public void goAvailable();
	public UserStatus getUserStatus();
	public AvailabilityTime getAvailabilityTime();
	public boolean isAvailable();
	public void givePermission(Caller aPermissable, Permissions aPermission);
	public void denyPermission(Caller aPermissable, Permissions aPermission);
	public boolean isMakingCall();
	public void setIsMakingCall(boolean isMakingCall);


}
