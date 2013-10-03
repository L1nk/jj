package com.wwc.jajing.sms.command;

import com.wwc.jajing.sms.receiver.WillCallBackReceiver;

public class JJWillCallBackCommand implements JJCommand {

	
	private WillCallBackReceiver willCallBackReceiver;

	
	public JJWillCallBackCommand(WillCallBackReceiver aWillCallBackReceiver) {
		this.willCallBackReceiver = aWillCallBackReceiver;
	}
	
	
	@Override
	public void execute() {
		if (this.willCallBackReceiver.isResponse()) {
			this.willCallBackReceiver.handleWillCallBackResponse();
		} else {
			this.willCallBackReceiver.handleWillCallBackRequest();
		}
	}

	@Override
	public String toString() {
		return "WIL_CALL_BACK";
	}

}
