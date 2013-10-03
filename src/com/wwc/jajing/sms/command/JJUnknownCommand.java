package com.wwc.jajing.sms.command;

import android.util.Log;

import com.wwc.jajing.domain.services.CallManager;
import com.wwc.jajing.sms.receiver.UnknownReceiver;
import com.wwc.jajing.system.JJSystemImpl;
import com.wwc.jajing.system.JJSystemImpl.Services;




public class JJUnknownCommand implements JJCommand {

	private static final String TAG = "JJUnknownCommand";
	
	private UnknownReceiver unknownCommandReceiver;
	
	public JJUnknownCommand(UnknownReceiver aReceiver)
	{
		this.unknownCommandReceiver = aReceiver;
	}
			
	@Override
	public void execute() {
		
		if (unknownCommandReceiver.isInitialMessage()){
			unknownCommandReceiver.handleInitialMessage();
		} else {
			Log.d(TAG, "Is not initial message.");
		}

	}
	
	@Override
	public String toString()
	{
		return "UNKNOWN_COMMAND";
	}
	
	



}
