package com.wwc.jajing.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class OutgoingCallReceiver extends BroadcastReceiver {

	String formattedNumber;
	Context context;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
		//outgoing phone number
        String number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
        formattedNumber = this.formatIncomingNumber(number);
        

	}
	
	/*
	 * This method is used to prevent
	 * REFACTOR this code, very inefficient way of formatting incoming number
	 */
	private String formatIncomingNumber(String incomingNumber)
	{
		if(incomingNumber.length() == 11){
			return incomingNumber;
		} else {
			if (!Character.toString(incomingNumber.charAt(0)).equalsIgnoreCase("1")){
				return ("1" + incomingNumber);
			}
		}
		return null;
		
	}


}
