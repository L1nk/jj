package com.wwc.jajing.sms.receiver;

import java.util.Date;

import android.content.Context;
import android.util.Log;

import com.wwc.jajing.domain.entity.MissedCall;
import com.wwc.jajing.sms.JJSMS;

public class HangUpReceiver implements CommandReceiver{

	private static final String TAG = "HangUpReceiver";
	private Context context;
	private JJSMS jjsms;

	public HangUpReceiver(Context aContext, JJSMS aJJSMS) {
		this.context = aContext;
		this.jjsms = aJJSMS;

	}
	
	@Override
	public boolean isResponse() {
		return this.jjsms.isResponse();
	}

	/*
	 * TODO - No okay response is sent, but we may add a response feature int eh future
	 */
	public void handleHangUpResponse() {
		//TODO - implement some sort of response handler here....
	}
	
	/*
	 * Just Logs that the caller hung up.
	 * TODO - we can send a response if we want to here, but for now its not needed. July 10, 2013
	 * 
	 */
	public void handleHangUpRequest() {
		MissedCall mc = new MissedCall(context, this.jjsms.getSendersPhoneNumber(), new Date(), MissedCall.Actions.HUNG_UP);
		mc.save();
		Log.d(TAG, "Finished handlling hang up request.");
	}

}
