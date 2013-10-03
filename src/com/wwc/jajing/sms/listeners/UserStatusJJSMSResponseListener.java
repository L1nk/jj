package com.wwc.jajing.sms.listeners;

import android.content.Context;
import android.util.Log;

import com.wwc.jajing.sms.JJSMS;
import com.wwc.jajing.sms.JJSMSManager;
import com.wwc.jajing.sms.JJSMSResponseDispatcher;
import com.wwc.jajing.sms.JJSMSResponseListener;
import com.wwc.jajing.sms.receiver.ShowOptionsToCallingPartyReceiver;
import com.wwc.jajing.system.JJSystemImpl;
import com.wwc.jajing.system.JJSystemImpl.Services;

public class UserStatusJJSMSResponseListener implements JJSMSResponseListener {

	private static final String TAG = "UserStatusJJSMSResponseListener";
	
	Context context = (Context) JJSystemImpl.getInstance().getSystemService(Services.CONTEXT);
	JJSMSManager jjsmsManager = (JJSMSManager) JJSystemImpl.getInstance().getSystemService(Services.SMS_MANAGER);
	JJSMSResponseDispatcher jjsmsDispatcher = jjsmsManager.getDispatcher();
	
	@Override
	public void handleJJSSMSResponse(JJSMS aJJSMSResponse) {
		if(!aJJSMSResponse.isResponse()) throw new IllegalArgumentException("JJSMS must be a response object.");
		
		//pass the reponse object to the receiver that knows how to show options to calling party
		ShowOptionsToCallingPartyReceiver aShowOptionsToCallingPartyReceiver = new ShowOptionsToCallingPartyReceiver(this.context, aJJSMSResponse);
		aShowOptionsToCallingPartyReceiver.showCallReceivingIsBusyOptionsMenu();
		Log.d(TAG, aJJSMSResponse.getRawJJSMS());
		Log.d(TAG, "Finished handling response, currently unregistered listners that handled response");
		jjsmsDispatcher.unregisterListener((String)aJJSMSResponse.getFromExtras("REQUESTID"));
		Log.d(TAG, "Finished unregistering listner that handled response");

		

	}

}
