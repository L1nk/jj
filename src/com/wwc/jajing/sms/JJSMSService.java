package com.wwc.jajing.sms;

import android.app.IntentService;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.wwc.jajing.sms.command.JJCommand;
import com.wwc.jajing.system.JJSystemImpl;
import com.wwc.jajing.system.JJSystemImpl.Services;

/*
 * This service should be running as long as it needs to finish handling the work 
 * for the JJSMSBroadcastReceiver
 * 
 */
public class JJSMSService extends IntentService {
	
	private static final String TAG = "JJSMSService";

	
	private JJSMSManager jjsmsManager = (JJSMSManager) JJSystemImpl.getInstance().getSystemService(Services.SMS_MANAGER);
	
	public JJSMSService() {
		super("JJSMSService");
	}



	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		
		Log.d(TAG, "JJSMS Intent Service has been created.");

	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		
		Log.d(TAG, "JJSMS Intent Service has been destroyed!");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(TAG, "JJSMS Intent Service has received an intent.");
		String jjSMSstr = intent.getStringExtra("jjsmsStr");
		String phoneNumber = intent.getStringExtra("phoneNumber");
		String formattedIncomingNumber = this.formatIncomingNumber(phoneNumber);
		
		Log.d(TAG, "jjsmsStr:" + jjSMSstr);
		Log.d(TAG, "phoneNumber:" + formattedIncomingNumber);
		
		if(jjSMSstr == null || formattedIncomingNumber == null ) return;
		
		Log.d(TAG, "A valid jjsms string was received. The rawSMSstr is:" + jjSMSstr);
		
		//create a JJSMS representation
		JJSMS jjsms = new JJSMS(jjSMSstr, new JJSMSSenderPhoneNumber(formattedIncomingNumber));
		
		//create the command
		JJCommand jjCommand = (JJCommand) jjsmsManager.getCommndFactory().createCommand(this, jjsms);
		
		//execute command
		jjCommand.execute();
		
		this.stopSelf();
		
		
	}
	
	
	
	/*
	 * This method is used to prevent
	 * REFACTOR this code, very inefficient way of formatting incoming number
	 */
	private String formatIncomingNumber(String incomingNumber)
	{
		if(incomingNumber.length() == 11){
			Log.d(TAG, "incoming number formatted is:" + incomingNumber);
			return incomingNumber;
		} else {
				//now check if first character  is +
			if(Character.toString(incomingNumber.charAt(0)).equalsIgnoreCase("+")) {
				return incomingNumber.replace("+", "");
			}
			if (!Character.toString(incomingNumber.charAt(0)).equalsIgnoreCase("1")){
				Log.d(TAG, "first character was:" + Character.toString(incomingNumber.charAt(0)));
				return ("1" + incomingNumber);
			}
		}
		return null;
		
	}

	


}
