package com.wwc.jajing.sms.receiver;

import android.content.Context;
import android.content.Intent;

import com.wwc.jajing.activities.OptionsToCallingParty;
import com.wwc.jajing.activities.PleaseWait;
import com.wwc.jajing.domain.services.CallManager;
import com.wwc.jajing.sms.JJSMS;
import com.wwc.jajing.sms.JJSMSResponseListener;
import com.wwc.jajing.sms.listeners.UserStatusJJSMSResponseListener;
import com.wwc.jajing.system.JJSystemImpl;
import com.wwc.jajing.system.JJSystemImpl.Services;

/*
 * This object does the actual work of invoking the ShowOptionsToCallingPartyCommand
 * 
 */
public class ShowOptionsToCallingPartyReceiver implements CommandReceiver {

	public static final String TAG = "ShowOptionsToCallingPartyReceiver";
	private Context context;
	private CallManager callManager;
	private JJSMS jjsms;
	private Class<?> activityClass = OptionsToCallingParty.class;



	public ShowOptionsToCallingPartyReceiver(Context aContext, JJSMS aJJSMS) {
		this.jjsms = aJJSMS;
		this.context = aContext;
		
		this.callManager = (CallManager) JJSystemImpl.getInstance().getSystemService(Services.CALL_MANAGER);
	}
	
	public void requestUserStatusToShowCallReceivingIsBusyOptionsMenu()
	{
		//THE VERY FIRST THING WE SHOULD DO BEFORE WE REQUEST USER STATUS IS DISCONNECT CALL
		//THIS WILL POSSIBLY SOLVE THE VOICEMAIL ISSUE, THAT IS BEING PROLONGED
		this.callManager.disconnectCall();
		
		
		
		UserStatusReceiver userStatusReceiver = new UserStatusReceiver(this.context, this.jjsms);
		JJSMSResponseListener aUserStatusJJSMSResponseListener = new UserStatusJJSMSResponseListener();
		
		//sends a request to keep track of
		userStatusReceiver.requestUserStatusJJSMS(aUserStatusJJSMSResponseListener);
		//after we send the request we may want to show progress dialog here
		PleaseWait.showPleaseWaitDialog(context, "Please Wait", "JaJing is processing your call...");
		
	}

	/*
	 * This method is reposnible for starting the activity that will display OptionsToCallingParty Activity.
	 * 
	 */
	public void showCallReceivingIsBusyOptionsMenu() {

		Intent i = new Intent(this.context, activityClass);
		i.putExtra(OptionsToCallingParty.JJSMS, jjsms);
		
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(i);

	}
	
	
	public boolean isResponse(){
		
		return this.jjsms.isResponse();
	}
}
