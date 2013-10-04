package com.wwc.jajing.sms;


/*
 * Responsible for sending raw jjsms messages to another phone's JaJing app.
 * 
 */
public interface JJSMSMessenger {
	
	public void sendRawSms(JJSMS jjsmsm, String phoneNumber);
}
