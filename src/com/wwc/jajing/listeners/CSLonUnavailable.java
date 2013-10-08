package com.wwc.jajing.listeners;

import java.util.Date;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.wwc.jajing.domain.entity.Caller;
import com.wwc.jajing.domain.entity.CallerImpl;
import com.wwc.jajing.domain.entity.MissedCall;
import com.wwc.jajing.domain.entity.TimeSetting;
import com.wwc.jajing.domain.entity.User;
import com.wwc.jajing.domain.services.CallManagerAbstract;
import com.wwc.jajing.domain.value.PhoneNumber;
import com.wwc.jajing.permissions.PermissionManager.Permissions;
import com.wwc.jajing.settings.time.TimeSettingTaskManager;
import com.wwc.jajing.sms.JJSMS;
import com.wwc.jajing.sms.JJSMSManager;
import com.wwc.jajing.sms.JJSMSMessenger;
import com.wwc.jajing.system.JJSystem;
import com.wwc.jajing.system.JJSystemImpl;
import com.wwc.jajing.system.JJSystemImpl.Services;

/*
 * Responsible for controlling the workflow of calls coming in and going out
 * 
 */
public class CSLonUnavailable extends PhoneStateListener {

	private static final String TAG = "CSLonUnavailable";
	private User user;
	private JJSMSManager jjsmsManger;
	
	private JJSMSMessenger jjsmsMessenger;
	
	private CallManagerAbstract callManager;
	
	private Context context;
	
	
	public CSLonUnavailable() 
	{
		super();
		//**CACHE OUR SYSTEM, FOR PERFORMANCE...
		JJSystem jjSystem = JJSystemImpl.getInstance();
		this.user = (User) jjSystem.getSystemService(
				Services.USER);
		
		this.jjsmsManger = (JJSMSManager) jjSystem.getSystemService(Services.SMS_MANAGER);
		
		if(this.jjsmsManger == null) {
			throw new IllegalStateException("jjsmsManager should not be null!");
		}
		this.jjsmsMessenger  = jjsmsManger.getMessenger();
		
		this.callManager = (CallManagerAbstract) jjSystem.getSystemService(Services.CALL_MANAGER);
		
		this.context =  (Context) jjSystem.getSystemService(Services.CONTEXT);
		
		
	}

	/*
	 * 
	 * This listener is registered when our JJOnAwayService is connected
	 * This listener is unregistered when our JJOnAwayService is disconnected.
	 * 
	 * This listener is responsible for Managing the workflow of incoming calls when user is "Unavailable"
	 * 
	 * 
	 */
	@Override
	public void onCallStateChanged(int state, final String incomingNumber) {
		super.onCallStateChanged(state, incomingNumber);

		//Formatting number because it was a souce of a MAJOR bug
		if(incomingNumber.length() == 0) return; //possible bug in android, sometimes incoming number is empty
		String formattedIncomingNumber = this.formatIncomingNumber(incomingNumber);
		Log.d(TAG, "formatted incoming number:" + formattedIncomingNumber);
		
		switch (state) {
		case TelephonyManager.CALL_STATE_RINGING:
			Log.d(TAG, "Ringing!");
			if (!user.isAvailable() && !this.user.isMakingCall()) {
				
				if(CallerImpl.isCallerPersisted(formattedIncomingNumber)) {
					Caller caller = CallerImpl.getCallerByPhoneNumber(formattedIncomingNumber);
					
					//check if the caller has "send call" permission set.
					if(caller.isPermissionSet(Permissions.SEND_CALL)){
						//check if an incoming caller has permission to SEND_CALL 
						if(caller.hasPermission(Permissions.SEND_CALL)){
							Log.d(TAG, "The caller has permission to call. Caller Id:" + caller.getId().toString());
							callManager.silenceRinger(false);
							this.user.denyPermission(caller, Permissions.SEND_CALL);
							//don't continue any further
							return;
						} else {
							Log.d(TAG, "The caller does not have permission to call!");
							
						}
					}
					//check if the caller has "leave voicemail" permission set.
					if(caller.isPermissionSet(Permissions.LEAVE_VOICEMAIL)){
						//check if an incoming caller has permission to LEAVE_VOICEMAIL 
						if(caller.hasPermission(Permissions.LEAVE_VOICEMAIL)){
							Log.d(TAG, "The caller has permission to leave-voicemail");
							this.callManager.sendCallToVoicemail();
							this.user.denyPermission(caller, Permissions.LEAVE_VOICEMAIL);
							//don't continue any further
							return;
						} else {
							Log.d(TAG, "Caller does not have permission to leave voicemail!");
							
							
						}
					} 
					
					//REGARDLESS IF THE CALLERS DOES OR DOES NOT HAVE APP, WE NEED TO DISCONNECT THE CALL IMMEDIATELY
					// THIS IS FOR CALL RECEIVING END
					callManager.disconnectCall();
					callManager.silenceRinger(true);
					
					 //SEND THE INITIAL MESSAGE IF WE ARE DEALING WITH A CALLER WITH NO APP
					Log.d(TAG, "Caller does NOT have the app");
					
					String availabilityTime = this.user.getUserStatus().getavailabilityTime();
					if(TimeSetting.hasEndTimePassed(availabilityTime)) availabilityTime = "00:00 --";
					availabilityTime = availabilityTime + " " + this.user.getAvailabilityTime().getTimeZoneString();
					// This indirectly check if other user has app installed, and forces callers who have the app to query for user status
					jjsmsMessenger.sendRawSms(new JJSMS(JJSMS.INITIAL_MESSAGE + this.user.getUserStatus().getAvailabilityStatus() + " and will be unavailable until " + availabilityTime.toLowerCase()), formattedIncomingNumber);
					Log.d(TAG, "JJSMS Initial Message Sent.");
					//Log the missed call
					this.logMissedCall(formattedIncomingNumber);
					
					return;

					
					
				}
				else {
					//Persist this caller
					Log.d(TAG, "Caller is NOT persisted! Persisting Caller...");
					//TODO - we don't want to assign every caller an anonmyous we need to give them a name later.
					CallerImpl aCaller = new CallerImpl(context, "Anonymous", formattedIncomingNumber);
					Log.d(TAG, "persisted phone number:" + formattedIncomingNumber);
					aCaller.save();
					Log.d(TAG, "Finished Persisting Caller! Incoming number was:" + formattedIncomingNumber);
					
					//Disconnect incoming call
					callManager.disconnectCall();
					callManager.silenceRinger(true);
					
					String availabilityTime = this.user.getUserStatus().getavailabilityTime();
					if(TimeSetting.hasEndTimePassed(availabilityTime)) availabilityTime = "00:00 --";
					availabilityTime = availabilityTime + " " + this.user.getAvailabilityTime().getTimeZoneString();
					// This indirectly check if other user has app installed, and forces callers who have the app to query for user status
					jjsmsMessenger.sendRawSms(new JJSMS(JJSMS.INITIAL_MESSAGE + this.user.getUserStatus().getAvailabilityStatus() + " and will be unavailable until " + availabilityTime.toLowerCase()), formattedIncomingNumber);
					Log.d(TAG, "The Default JJSMS Initial Message Was Sent.");
					//log missed call
					this.logMissedCall(formattedIncomingNumber);
					
					
				}
				
				
				

			} else {
				Log.d(TAG, "User is AVAILABLE! OR is MAKING A CALL!");
				this.user.setIsMakingCall(false);
				//LET THE CALL GO THROUGH
			}
			Log.d(TAG, "RINGING. CSLonUnavailable. User Availability Status:" + user.getUserStatus().getAvailabilityStatus() );
			break;
			
		case TelephonyManager.CALL_STATE_IDLE:
			Log.d(TAG, "call state idle");
			this.user.setIsMakingCall(false);

			break;
		case TelephonyManager.CALL_STATE_OFFHOOK:
			Log.d(TAG, "call state offhook");
			this.user.setIsMakingCall(false);

			break;
		
		}

	}
	
	private void logMissedCall(String incomingNumber)
	{
		//Log the missed call
		MissedCall mc = new MissedCall(this.context, new PhoneNumber(incomingNumber), new Date(), MissedCall.Actions.NO_APP);
		mc.save();
		
	}
	
	
	/*
	 * This method is used to prevent
	 * REFACTOR this code, very inefficient way of formatting incoming number
	 * This code is duplicated in JJSMSService....
	 */
	private String formatIncomingNumber(String incomingNumber)
	{
		if(incomingNumber.length() == 11){
			return incomingNumber;
		} else {
				//now check if first character  is +
			if(Character.toString(incomingNumber.charAt(0)).equalsIgnoreCase("+")) {
				return incomingNumber.replace("+", " ");
			}
			if (!Character.toString(incomingNumber.charAt(0)).equalsIgnoreCase("1")){
				Log.d(TAG, "first character was:" + Character.toString(incomingNumber.charAt(0)));
				return ("1" + incomingNumber);
			}
		}
		return null;
		
	}


}
