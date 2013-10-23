package com.wwc.jajing.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.wwc.jajing.domain.entity.User;
import com.wwc.jajing.system.JJSystem;
import com.wwc.jajing.system.JJSystemImpl;
import com.wwc.jajing.system.JJSystemImpl.Services;

/*
 * Responsible for listening to raw JJSMS messages
 */
public class JJSMSBroadcastReceiver extends BroadcastReceiver{

	private final String TAG = "JJSMSBroadcastReceiver";
	
	
	private JJSMSManager jjsmsManager;
	private JJSMSHelper jjsmsHelper;
	private JJSMSValidator jjsmsValidator;
	private User user; 
	
	public JJSMSBroadcastReceiver()
	{
		//CACHE SYSTEM FOR PERFORMANCE
		JJSystem jjSystem = JJSystemImpl.getInstance();
		this.user = (User) jjSystem.getSystemService(Services.USER);
		this.jjsmsManager = (JJSMSManager) jjSystem.getSystemService(Services.SMS_MANAGER);
		//BUG CRASHING AT THIS LINE JJSMS MANAGER is null
		this.jjsmsHelper = jjsmsManager.getHelper();
		this.jjsmsValidator = jjsmsManager.getValidator();
		
	}
	/*
	 * This method will check to see if we received any raw jjsms strings
	 * If so it will create these messages into a JJSMS representations, and use these messages to execute commands.
	 *
	 */
	@Override
	public void onReceive(Context context, Intent intent) {

        String rawSMSstr = jjsmsHelper.getMessageBodyFromSMS(intent);
        String phoneNumber = jjsmsHelper.getPhoneNumberFromIncomingSMS(intent);

        String formattedIncomingNumber = this.formatIncomingNumber(phoneNumber);

        jjsmsManager.getMessenger()
                .sendRawSms(new JJSMS(JJSMS.INITIAL_MESSAGE +
                            this.user.getUserStatus().getAvailabilityStatus() +
                            " and will be unavailable until " +
                            this.user.getUserStatus().getavailabilityTime().toLowerCase()),
                            formattedIncomingNumber);
		

		Log.d(TAG, "BR got it" + rawSMSstr);
		
		if(jjsmsValidator.isValidRawJJSMSStr(rawSMSstr)){
			
			Intent i = new Intent(context, JJSMSService.class);
			i.putExtra("jjsmsStr", rawSMSstr);
			i.putExtra("phoneNumber", phoneNumber);
			context.startService(i);
		
			abortBroadcast();
			
		} else {
			Log.d(TAG, "A invalid jjsms string was received.The rawSMSstr is:" + rawSMSstr);
			// "Invalid" messages are just normal sms messages sent to the user by his contacts
			// We broadcast an intent, and attach associated data to the intent to deliver the messages another time
			// let the SMSReceiver to do the work
			
			//check if the user is unavailable before aborting the message
			if (!this.user.isAvailable()) {
				Intent smsIntent = new Intent().setAction(SMSReceiver.INTENT_ACTION);
				smsIntent.putExtra("textMessage", rawSMSstr);
				smsIntent.putExtra("phoneNumber", phoneNumber);
				context.sendBroadcast(smsIntent);
				
				//prevent these sms messages from going into user's inbox
				abortBroadcast();
			}
			
			
			
		}

	}

    private String formatIncomingNumber(String incomingNumber)
    {
        if(incomingNumber.length() == 11){
            return incomingNumber;
        } else {
            //now check if first character  is +
            if(Character.toString(incomingNumber.charAt(0)).equalsIgnoreCase("+")) {
                return incomingNumber.replace("+", " ");
            }
            if (!Character.toString(incomingNumber.charAt(0)).equalsIgnoreCase("1")){
                Log.d(TAG, "first character was:" + Character.toString(incomingNumber.charAt(0)));
                return ("1" + incomingNumber);
            }
        }
        return null;

    }


}
