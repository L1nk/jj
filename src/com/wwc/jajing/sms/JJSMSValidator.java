package com.wwc.jajing.sms;

/*
 * This class is responsible for validating weather raw sms strings are valid jjsms messages
 * 
 */
public interface JJSMSValidator {
	
	public boolean isValidRawJJSMSStr(String rawSMSstr);
	
}
