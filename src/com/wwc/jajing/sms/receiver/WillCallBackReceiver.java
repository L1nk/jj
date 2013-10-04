package com.wwc.jajing.sms.receiver;

import java.util.Date;

import android.content.Context;
import android.util.Log;

import com.wwc.jajing.domain.entity.MissedCall;
import com.wwc.jajing.sms.JJSMS;

public class WillCallBackReceiver implements CommandReceiver{

	private static final String TAG = "WillCallBackReceiver";
	private Context context;
	private JJSMS jjsms;

	public WillCallBackReceiver(Context aContext, JJSMS aJJSMS) {
		this.context = aContext;
		this.jjsms = aJJSMS;

	}
	
	@Override
	public boolean isResponse() {
		return this.jjsms.isResponse();
	}

	//optionaly handle a response here...
	public void handleWillCallBackResponse() {
		// TODO Auto-generated method stub
		
	}


	//TODO - we can optionally send a response if we want to here...
	public void handleWillCallBackRequest() {
		MissedCall mc = new MissedCall(context, this.jjsms.getSendersPhoneNumber(), new Date(), MissedCall.Actions.WILL_CALL_BACK);
		mc.save();
		Log.d(TAG, "Finished handlling WILL CALL BACK request.");
	}

}
