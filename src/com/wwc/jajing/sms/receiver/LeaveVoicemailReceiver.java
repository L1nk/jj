package com.wwc.jajing.sms.receiver;

import java.util.Date;

import android.content.Context;
import android.util.Log;

import com.wwc.jajing.domain.entity.Caller;
import com.wwc.jajing.domain.entity.CallerImpl;
import com.wwc.jajing.domain.entity.MissedCall;
import com.wwc.jajing.domain.entity.User;
import com.wwc.jajing.permissions.PermissionManager.Permissions;
import com.wwc.jajing.sms.JJSMS;
import com.wwc.jajing.sms.JJSMSManager;
import com.wwc.jajing.sms.JJSMSMessenger;
import com.wwc.jajing.system.JJSystemImpl;
import com.wwc.jajing.system.JJSystemImpl.Services;

public class LeaveVoicemailReceiver implements CommandReceiver{


	private static final String TAG = "LeaveVoicemailReceiver";
	private Context context;
	private JJSMS jjsms;

	private User user = (User) JJSystemImpl.getInstance().getSystemService(Services.USER);
	private JJSMSManager jjsmsManager = (JJSMSManager) JJSystemImpl.getInstance().getSystemService(Services.SMS_MANAGER);
	private JJSMSMessenger jjsmsMessenger = jjsmsManager.getMessenger();
	
	public LeaveVoicemailReceiver(Context aContext, JJSMS aJJSMS) {
		this.context = aContext;
		this.jjsms = aJJSMS;

	}
	
	
	
	/*
	 * This method does 3 things:
	 * Gives the caller permission to leave a voicemail
	 * Logs the call as a missed call
	 * Sends an okay response signaling caller's app to make the call.
	 * 
	 * 
	 * 
	 * 
	 */
	public void handleLeaveVoicemailRequest() {
		//get the caller
		Caller aCaller = CallerImpl.getCallerByPhoneNumber(this.jjsms.getSendersPhoneNumber().toString());
		if(aCaller == null) {
			throw new NullPointerException("Caller can't be null. Maybe you have the incorrect phone number when looking up the caller?");
		}
		//give the caller permission to leave voiceail
		this.user.givePermission(aCaller, Permissions.LEAVE_VOICEMAIL);
		//Log the call as a missed call
		this.logMissedCallByVoicemail();
		this.user.setIsMakingCall(false);//because we are expecting a call
		//Send an okay response to caller, sinaling caller's app to make the call fore him
		this.sendLeaveVoicemailOKResponse();
		
		Log.d(TAG, "Finished  handling leave voicemail request.");

	}
	
	private void logMissedCallByVoicemail()
	{
		MissedCall mc = new MissedCall(context, this.jjsms.getSendersPhoneNumber(), new Date(), MissedCall.Actions.LEFT_VOICEMAIL);
		mc.save();
		
	}
	
	private void sendLeaveVoicemailOKResponse()
	{
		this.jjsmsMessenger.sendRawSms(new JJSMS("#jj/LEAVE_VOICEMAIL?ISRESPONSE=TRUE"), this.jjsms.getSendersPhoneNumber().toString());
		Log.d(TAG, "Finished sending OK response");

	}
	
	/*
	 * This method is responsible for handling leave voicemail OK resposnes
	 * 
	 * 
	 */
	public void handleLeaveVoicemailResponse()
	{
		//send call to the call-receiver
		this.user.makeCall(this.jjsms.getSendersPhoneNumber());
		//reset flas after we make call
		this.user.setIsMakingCall(false);
		Log.d(TAG, "Finished handling OK response");
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@Override
	public boolean isResponse() {
		return this.jjsms.isResponse();
	}





}
