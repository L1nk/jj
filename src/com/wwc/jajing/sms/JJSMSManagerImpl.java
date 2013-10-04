package com.wwc.jajing.sms;

import com.wwc.jajing.sms.command.JJCommandFactory;

/*
 * This class is responsible for PROVIDING JJSMS Factories, Helpers, Validators, and whatever components/data we want publicly exposed.
 * 
 */
public class JJSMSManagerImpl implements JJSMSManager {

	private JJSMSHelper jjsmsHelper;
	private JJSMSValidator jjsmsValidator;
	private JJSMSMessenger jjsmsMessenger;
	private JJCommandFactory jjCommandFactory;
	private JJSMSResponseDispatcher jjsmsDispatcher;
	
	public JJSMSManagerImpl(JJSMSHelper jjsmsHelper, JJSMSValidator jjsmsValidator, JJSMSMessenger jjsmsMessenger, JJCommandFactory aCommandFacotory, JJSMSResponseDispatcher aJJSMSResponseDospatcher)
	{
		this.jjsmsHelper = jjsmsHelper;
		this.jjsmsValidator = jjsmsValidator;
		this.jjsmsMessenger = jjsmsMessenger;
		this.jjCommandFactory = aCommandFacotory;
		this.jjsmsDispatcher = aJJSMSResponseDospatcher;
	}
	



	@Override
	public JJSMSHelper getHelper() {
		return this.jjsmsHelper;
	}


	@Override
	public JJSMSValidator getValidator() {
		return this.jjsmsValidator;
	}


	@Override
	public JJSMSMessenger getMessenger() {
		return this.jjsmsMessenger;
	}




	@Override
	public JJCommandFactory getCommndFactory() {
		
		return this.jjCommandFactory;
	}




	@Override
	public JJSMSResponseDispatcher getDispatcher() {
		return this.jjsmsDispatcher;
	}
	
	

}
