package com.wwc.jajing.sms.command;

import com.wwc.jajing.sms.receiver.LeaveVoicemailReceiver;



public class JJLeaveVoicemailCommand implements JJCommand{

	private LeaveVoicemailReceiver leaveVoicemailReceiver;
	
	public JJLeaveVoicemailCommand(LeaveVoicemailReceiver aLeaveVoicemailReceiver)
	{
		this.leaveVoicemailReceiver = aLeaveVoicemailReceiver;
	}

	@Override
	public void execute() {
		if(this.leaveVoicemailReceiver.isResponse()) {
			this.leaveVoicemailReceiver.handleLeaveVoicemailResponse();
		} else {
			this.leaveVoicemailReceiver.handleLeaveVoicemailRequest();
		}
	}
	
	@Override
	public String toString()
	{
		return "LEAVE_VOICEMAIL";
	}




}
