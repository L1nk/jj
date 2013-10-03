package com.wwc.jajing.sms.command;

import android.content.Context;

import com.wwc.jajing.sms.JJSMS;
import com.wwc.jajing.sms.command.meta.JJUserStatus;
import com.wwc.jajing.sms.receiver.DisturbReceiver;
import com.wwc.jajing.sms.receiver.HangUpReceiver;
import com.wwc.jajing.sms.receiver.LeaveVoicemailReceiver;
import com.wwc.jajing.sms.receiver.ShowOptionsToCallingPartyReceiver;
import com.wwc.jajing.sms.receiver.UnknownReceiver;
import com.wwc.jajing.sms.receiver.UserStatusReceiver;
import com.wwc.jajing.sms.receiver.WillCallBackReceiver;




public class JJCommandFactoryImpl implements JJCommandFactory{

	
	@Override
	public JJCommand createCommand(Context aContext, JJSMS aJJSMS) {
		Commands aCommand = aJJSMS.getCommand();
		
		switch(aCommand)
		{
		case DISTURB:
			DisturbReceiver aDisturbReceiver = new DisturbReceiver(aContext, aJJSMS);
			return new JJDisturbCommand(aDisturbReceiver);
		case LEAVE_VOICEMAIL:
			LeaveVoicemailReceiver aLeaveVoicemailReceiver = new LeaveVoicemailReceiver(aContext, aJJSMS);
			return new JJLeaveVoicemailCommand(aLeaveVoicemailReceiver);
		case LEAVE_SMS_MESSAGE:
			return new JJLeaveSmsMessageCommand();
		case WILL_CALL_BACK:
			WillCallBackReceiver aWillCallBackReceiver = new WillCallBackReceiver(aContext, aJJSMS);
			return new JJWillCallBackCommand(aWillCallBackReceiver);
		case HANG_UP:
			HangUpReceiver aHangUpReceiver = new HangUpReceiver(aContext, aJJSMS);
			return new JJHangUpCommand(aHangUpReceiver);
		case SHOW_OPTIONS_TO_CALLING_PARTY:
			ShowOptionsToCallingPartyReceiver aShowOptionsToCallingPartyReceiver = new ShowOptionsToCallingPartyReceiver(aContext, aJJSMS);
			return new JJShowOptionsToCallingPartyCommand(aShowOptionsToCallingPartyReceiver);
		case USER_STATUS:
			UserStatusReceiver aUserStatusReceiver = new UserStatusReceiver(aContext, aJJSMS);
			return new JJUserStatus(aUserStatusReceiver);
		default:
			UnknownReceiver aReceiver = new UnknownReceiver(aContext, aJJSMS);
			return new JJUnknownCommand(aReceiver);
			
		}
	}
	
	
	public enum  Commands {
		DISTURB  {
			@Override
	        public String toString() {
				return "DISTURB";
			}
		}, 
		WILL_CALL_BACK {
			@Override
		    public String toString() {
				return "WILL_CALL_BACK";
			}
		}, 
		LEAVE_SMS_MESSAGE{
			@Override
			public String toString() {
				return "LEAVE_SMS_MESSAGE";
			}
		},
		LEAVE_VOICEMAIL {
			@Override
			public String toString() {
				return "LEAVE_VOICEMAIL";
			}
		},
		HANG_UP {
			@Override
	        public String toString() {
				return "HANG_UP";
			}
		},
		USER_STATUS {
			@Override
	        public String toString() {
				return "USER_STATUS";
			}
		},
		SHOW_OPTIONS_TO_CALLING_PARTY {
			@Override
	        public String toString() {
				return "SHOW_OPTIONS_TO_CALLING_PARTY";
			}
		},
		UNKNOWN_COMMAND {
			@Override
	        public String toString() {
				return "UNKNOWN_COMMAND";
			}
		}
	}

	
	
	
	
	
	
	
	
	
	
	
	
	

}
