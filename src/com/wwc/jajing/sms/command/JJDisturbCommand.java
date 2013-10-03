package com.wwc.jajing.sms.command;

import com.wwc.jajing.sms.receiver.DisturbReceiver;



public class JJDisturbCommand implements JJCommand{

	//optionally specify receiver
	
	DisturbReceiver receiver;
	
	public JJDisturbCommand(DisturbReceiver aDisturbReceiver)
	{
		this.receiver = aDisturbReceiver;
	}
	
	@Override
	public void execute() {
		if(!receiver.isResponse()) {
			// is a request, so show Allow/Deny options
			this.receiver.showWantsToDisturbActivity();
		} else {
			this.receiver.showResponseDialog();
		}	

	}
	
	@Override
	public String toString()
	{
		return "DISTURB";
		
	}




}
