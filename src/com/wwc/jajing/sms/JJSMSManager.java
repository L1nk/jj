package com.wwc.jajing.sms;

import com.wwc.jajing.sms.command.JJCommandFactory;

public interface JJSMSManager {
	
	public JJSMSHelper getHelper();
	public JJSMSValidator getValidator();
	public JJSMSMessenger getMessenger();
	public JJCommandFactory getCommndFactory();
	public JJSMSResponseDispatcher getDispatcher();
	
}
