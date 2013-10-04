package com.wwc.jajing.listeners;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.wwc.jajing.domain.entity.User;
import com.wwc.jajing.domain.services.CallManagerAbstract;
import com.wwc.jajing.sms.JJSMSManager;
import com.wwc.jajing.sms.JJSMSMessenger;
import com.wwc.jajing.system.JJSystem;
import com.wwc.jajing.system.JJSystemImpl;
import com.wwc.jajing.system.JJSystemImpl.Services;

public class CSLonAvailable extends PhoneStateListener{
	
	private static final String TAG = "CSLonAvailable";

	private User user;
	private CallManagerAbstract callManager;
	private JJSMSManager jjsmsManger;
	

	public CSLonAvailable()
	{
		super();
		//CACHE SYSTEM FOR PERFORMANCE
		JJSystem jjSystem = JJSystemImpl.getInstance();
		this.user = (User) jjSystem.getSystemService(Services.USER);
		this.callManager = (CallManagerAbstract) jjSystem.getSystemService(Services.CALL_MANAGER);
		this.jjsmsManger = (JJSMSManager) jjSystem.getSystemService(Services.SMS_MANAGER);
	}
	
	/*
	 * 
	 * This listener is registered when our JJOnAwayService is disconnected
	 * This listener is unregistered when our JJOnAwayService is connected.
	 * 
	 * This listener is responsible for Managing the workflow of incoming calls when user is "Unavailable"
	 * 
	 * 
	 */
	@Override
	public void onCallStateChanged(int state, String incomingNumber) {
		super.onCallStateChanged(state, incomingNumber);

		switch (state) {
		case TelephonyManager.CALL_STATE_RINGING:

			if (user.getUserStatus().getAvailabilityStatus().equals("AVAILABLE")) {
				
				//unsilence ringer
				callManager.silenceRinger(false);
				
				

			} else {
				

			}
			Log.d(TAG, "RINGING.CSLonAvailable. User Availability Status: " + user.getUserStatus().getAvailabilityStatus());
			// if not busy, let call go through normally
			break;
		}

	}
}
