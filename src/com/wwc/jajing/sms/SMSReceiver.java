package com.wwc.jajing.sms;

import java.util.Date;

import com.wwc.jajing.domain.entity.MissedMessage;
import com.wwc.jajing.domain.entity.MissedMessage.Actions;
import com.wwc.jajing.domain.services.CallManager;
import com.wwc.jajing.domain.value.PhoneNumber;
import com.wwc.jajing.system.JJSystemImpl;
import com.wwc.jajing.system.JJSystemImpl.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/*
 * This receiver is responsible for storing missed text messsages that were not delived to inbox
 * 
 */
public class SMSReceiver extends BroadcastReceiver {

	public static final String INTENT_ACTION = "com.exmaple.jajingprototype.sms.NEW_SMS";
	
	private CallManager cm;
	
	
	public SMSReceiver()
	{
		cm = (CallManager) JJSystemImpl.getInstance().getSystemService(Services.CALL_MANAGER);
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		String textMessage = intent.getStringExtra("textMessage");
		String phoneNumber = intent.getStringExtra("phoneNumber");
		Date dateNow = new Date();
		
		//save the missed message
		MissedMessage mm = new MissedMessage(context, new PhoneNumber(phoneNumber), dateNow, Actions.MISSED_MESSAGE, textMessage);
		mm.save();
		
		
	}

}
