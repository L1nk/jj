package com.wwc.jajing.system;

import android.content.Context;
import android.content.Intent;

import com.wwc.jajing.domain.entity.User;
import com.wwc.jajing.domain.entity.UserImpl;
import com.wwc.jajing.domain.services.CallManager;
import com.wwc.jajing.domain.services.CallManagerAbstract;
import com.wwc.jajing.settings.time.DailyNotifier;
import com.wwc.jajing.sms.JJSMSHelper;
import com.wwc.jajing.sms.JJSMSManager;
import com.wwc.jajing.sms.JJSMSManagerImpl;
import com.wwc.jajing.sms.JJSMSMessenger;
import com.wwc.jajing.sms.JJSMSMessengerImpl;
import com.wwc.jajing.sms.JJSMSResponseDispatcher;
import com.wwc.jajing.sms.JJSMSResponseDispatcherImpl;
import com.wwc.jajing.sms.JJSMSValidator;
import com.wwc.jajing.sms.JJSMSValidatorImpl;
import com.wwc.jajing.sms.command.JJCommandFactory;
import com.wwc.jajing.sms.command.JJCommandFactoryImpl;

/*
 * Singleton
 */

public class JJSystemImpl implements JJSystem {

	public enum Services {
		SMS_MANAGER,CALL_MANAGER, CONTEXT, USER,
	}

	public static final String INTENT_INIT_COMPLETE = "com.exmaple.jajingprototype.system.event.INIT_COMPLETE";
	private static final JJRegistry jjregistry = JJRegistryImpl.getInstance();
	private static final JJSystem instance = new JJSystemImpl();
	private static boolean hasBeenInitialized;

	private JJSystemImpl() {
		
	}
	
	public static JJSystem getInstance()
	{
		return instance;
	}

	@Override
	public Object getSystemService(Services service) {

		return jjregistry.getFromRegistry(service);
	}
	
	public static boolean hasBeenIntialized()
	{
		if(hasBeenInitialized == true) {
			return true;
		}
		return false;
	}

	/*
	 * Here we perform any initial bootstrap work for our system. 
	 * ** registering global services into our registry. **
	 */
	public void initSystem(Context context) {
		
		
		JJSMSHelper jjsmsHelper = new JJSMSHelper();
		JJSMSValidator jjsmsValidator = new JJSMSValidatorImpl();
		JJSMSMessenger jjsmsMessenger = new JJSMSMessengerImpl();
		JJCommandFactory jjCommandFactory = new JJCommandFactoryImpl();
		JJSMSResponseDispatcher jjDispatcher = new JJSMSResponseDispatcherImpl();
		
		JJSMSManager jjsmsManager = new JJSMSManagerImpl(jjsmsHelper, jjsmsValidator, jjsmsMessenger, jjCommandFactory, jjDispatcher);
		
		//Context must be added before any of the other services are added to our registry
		//add Context to our registry
		this.jjregistry.addToRegistry(Services.CONTEXT, context);
		
		//add SMS manager to registry
		this.jjregistry.addToRegistry(Services.SMS_MANAGER, jjsmsManager);
		
		//add User to registry
		//this restores the state of the user if the user is defined
		User user = UserImpl.findById(UserImpl.class, Long.parseLong("1"));
		this.jjregistry.addToRegistry(Services.USER, (user == null)? UserImpl.getInstance():user);		
		
		//add CallManager to registry
		CallManagerAbstract callManger = new CallManager();
		this.jjregistry.addToRegistry(Services.CALL_MANAGER, callManger);
		
		//fire event letting components know system is done initializing
		Intent intent = new Intent();
		intent.setAction(this.INTENT_INIT_COMPLETE);
		context.sendBroadcast(intent);
				
		this.hasBeenInitialized = true;
		
		
		
	}

}
