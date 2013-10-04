package com.wwc.jajing.sms.command;

import com.wwc.jajing.sms.receiver.HangUpReceiver;

public class JJHangUpCommand implements JJCommand {

	public static final String TAG = "JJHangUpCommand";

	private HangUpReceiver hangUpReceiver;

	public JJHangUpCommand(HangUpReceiver aHangUpReceiver) {
		this.hangUpReceiver = aHangUpReceiver;
	}

	
	
	@Override
	public void execute() {
		if (this.hangUpReceiver.isResponse()) {
			this.hangUpReceiver.handleHangUpResponse();
		} else {
			this.hangUpReceiver.handleHangUpRequest();
		}
	}

	@Override
	public String toString() {
		return "HANG_UP";
	}

}
