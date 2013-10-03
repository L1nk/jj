package com.wwc.jajing.sms.command;

import com.wwc.jajing.sms.JJSMSManager;
import com.wwc.jajing.system.JJSystemImpl;
import com.wwc.jajing.system.JJSystemImpl.Services;

/*
 * This class know how to perform the command 
 * 
 */
public class JJCallReceivingBusyReceiver implements JJCommand{

	private final String TAG = "JJCallReceivingBusy";
	private JJSMSManager jjsmsManager = (JJSMSManager) JJSystemImpl.getInstance().getSystemService(Services.SMS_MANAGER);
	

	

	/*
	 * Executed when call-receiving wants to execute an action on the callers phone.
	 * 
	 */
	@Override
	public void execute() {
		
		//**sample test**/
		
		//create a jjsms telling the caller's JJ_APP the call-receiving user is busy
		
		
		//**this is the jjsms mesage we are going to send to the caller's phone
		
		/*
		 * TODO - createJJSMS should be refactored. Awkward passing the context to JJSMS just for the sake of passing it to the
		 * action...Facotry has two responsibilities, we should create a new class that handles the formatting, reading of jjsms strings.
		 * 
		 */
		
		//JJSMS jjsms = jjsmsManager.getFactory().createJJSMS(context, "#jj/show_caller_is_busy_menu?key1=val1");
		
		//**
		
		//Log.d(TAG, extras.get("originatingNumber") );
		//jjsmsManager.getMessenger().sendRawSms(jjsms, extras.get("originatingNumber"));
		
		
	}
	
	@Override
	public String toString()
	{
		return "CALL_RECEIVING_BUSY";
	}



}
