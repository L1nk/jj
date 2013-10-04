package com.wwc.jajing.sms.command;

import android.util.Log;

import com.wwc.jajing.sms.receiver.ShowOptionsToCallingPartyReceiver;


public class JJShowOptionsToCallingPartyCommand implements JJCommand {

	private final String TAG = "JJShowOptionsToCallingPartyCommand";
		
	ShowOptionsToCallingPartyReceiver receiver;
	
	public JJShowOptionsToCallingPartyCommand(ShowOptionsToCallingPartyReceiver aReceiver)
	{
		this.receiver = aReceiver;
	}
	
	@Override
	public void execute() {
		if (this.receiver.isResponse()) {
			//TODO - this code is possibly never reached, possible source of bug....
			receiver.showCallReceivingIsBusyOptionsMenu();
		} else {
			receiver.requestUserStatusToShowCallReceivingIsBusyOptionsMenu();
		}
		
	}
	
	@Override
	public String toString()
	{
		return "SHOW_OPTIONS_TO_CALLING_PARTY";
	}





}
