package com.wwc.jajing.sms.receiver;

import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.wwc.jajing.activities.PleaseWait;
import com.wwc.jajing.domain.entity.Caller;
import com.wwc.jajing.domain.entity.CallerImpl;
import com.wwc.jajing.domain.entity.MissedCall;
import com.wwc.jajing.domain.entity.User;
import com.wwc.jajing.domain.services.CallManagerAbstract;
import com.wwc.jajing.domain.value.UserStatus;
import com.wwc.jajing.sms.JJSMS;
import com.wwc.jajing.sms.JJSMSManager;
import com.wwc.jajing.sms.JJSMSMessenger;
import com.wwc.jajing.sms.JJSMSResponseDispatcher;
import com.wwc.jajing.sms.JJSMSResponseListener;
import com.wwc.jajing.system.JJSystemImpl;
import com.wwc.jajing.system.JJSystemImpl.Services;

public class UserStatusReceiver implements CommandReceiver {

	private static final String TAG = "UserStatusReceiver";
	private Context context;
	private JJSMS jjsms;

	User user = (User) JJSystemImpl.getInstance().getSystemService(
			Services.USER);
	JJSMSManager jjsmsManager = (JJSMSManager) JJSystemImpl.getInstance()
			.getSystemService(Services.SMS_MANAGER);
	JJSMSMessenger jjsmsMessenger = jjsmsManager.getMessenger();
	
	private ShowOptionsToCallingPartyReceiver showOptionsToCallingPartyReceiver;
	private CallManagerAbstract cm = (CallManagerAbstract) JJSystemImpl.getInstance().getSystemService(Services.CALL_MANAGER);



	public UserStatusReceiver(Context aContext, JJSMS aJJSMS) {
		this.context = aContext;
		this.jjsms = aJJSMS;

		
	}
	
	
	public boolean isResponse(){
		
		return this.jjsms.isResponse();
	}

	/*
	 * Send the user status to the client that requested it.
	 * 
	 * 
	 * ## Note - The JJSMS object is a REQUEST object by default.
	 */
	public void sendUserStatusJJSMS() {
			
		if (jjsms.getFromExtras("REQUESTID") == "UNKNOWN_EXTRA") {
			throw new IllegalArgumentException("REQUEST ID CANNOT BE UNKNOWN");
		}
			String requestId = (String) jjsms.getFromExtras("REQUESTID");
			
			UserStatus us = user.getUserStatus();
			String jjsmsResponseStr = "#detach/USER_STATUS?availabilityStatus="
					+ us.getAvailabilityStatus() + ",availabilityTime="
					+ us.getavailabilityTime() + ",isResponse=True,requestId=" + requestId;
			JJSMS jjsmsResponse = new JJSMS(jjsmsResponseStr);
			jjsmsMessenger.sendRawSms(jjsmsResponse, jjsms
					.getSendersPhoneNumber().toString());
			
			Log.d(TAG, "Finished sending the user status to the client that requested it.");
	

	}
	
	/*
	 * Delegates handling of jjsmsResponse to the JJSMSResponseListener that was registred to handl the request, when the request was sent
	 * 
	 */
	public void handleUserStatusResponse()
	{
		Log.d(TAG, "Received response for UserStatus");
		//get our dispatcher
		JJSMSResponseDispatcher jjsmsResponseDispatcher = this.jjsmsManager.getDispatcher();
		jjsmsResponseDispatcher.handleResponse(this.jjsms);

	}
	
	/*
	 * 
	 * Requests the UserStatus.
	 * 
	 * 
	 * ## Note - The JJSMS object is a REQUEST object by default.
	 */
	public void requestUserStatusJJSMS(JJSMSResponseListener aJJSMSResponseListner) {
		//disconnect call before requesting user status
		Log.d(TAG, "Disconnecting call before sending a request for the user status.");
		cm.disconnectCall();
		
		PleaseWait.showPleaseWaitDialog(context, "Please Wait", "we are attempting to get information from " + this.jjsms.getSendersPhoneNumber().toString());
		  
		//generate random request id to keep track of this request
		String requestId  = String.valueOf(Math.abs(new Random().nextInt()));
		//get our dispatcher
		JJSMSResponseDispatcher jjsmsResponseDispatcher = this.jjsmsManager.getDispatcher();
		
		//register our listener this will handle the response when we get it.
		jjsmsResponseDispatcher.registerListener(requestId, aJJSMSResponseListner);
		
		
		String requestJJSMSstr = "#detach/USER_STATUS?REQUESTID=" + requestId;
		jjsmsMessenger.sendRawSms(new JJSMS(requestJJSMSstr), this.jjsms.getSendersPhoneNumber().toString());
		
		Log.d(TAG, "Sent request for UserStatus");

		this.showProgressDialog();

	}
	
	private void showProgressDialog()
	{
		Intent i = new Intent(context, PleaseWait.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.putExtra("title", "detach is connecting....");
		i.putExtra("description", "please wait...");

		
		context.startActivity(i);
	}
	
	
	/*
	 * This changes the callers app staus 
	 */
	public void changeCallerHasAppStatus(Boolean hasApp){
		
		Caller aCaller = CallerImpl.getCallerByPhoneNumber(this.jjsms.getSendersPhoneNumber().toString());
		if(aCaller == null) {
			
			throw new NullPointerException("Caller can't be null, maybe the number persisted is incorrect. You most likely have a formatting problem.");
			
		} 
		aCaller.setHasApp(true);
	}
	
	public void deleteNoAppLogsByPhoneNumber() {
		MissedCall.delteNoAppLogsByPhoneNumber(this.jjsms.getSendersPhoneNumber());
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
