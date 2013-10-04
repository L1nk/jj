package com.wwc.jajing.sms.command.meta;

import android.util.Log;

import com.wwc.jajing.domain.entity.MissedCall;
import com.wwc.jajing.sms.command.JJCommand;
import com.wwc.jajing.sms.receiver.UserStatusReceiver;


/*
 * We send our user's status to the client requesting this information.
 * 
 */
public class JJUserStatus implements JJCommand{

	private static final String TAG = "JJUserStatus";
	UserStatusReceiver receiver;
	
	public JJUserStatus(UserStatusReceiver aReceiver)
	{
		this.receiver = aReceiver;
	}
	
	@Override
	public void execute() {
		if(receiver.isResponse()){//getting response data
			Log.d(TAG, "We are executing a command that is going to handle response data.");
			receiver.handleUserStatusResponse();
		} else {//sending UserStatus data
			
			Log.d(TAG, "We are executing a command that is sending UserStatus info to client.");
			receiver.sendUserStatusJJSMS();
			//since we are sending a request for response data this means the caller that requested this command
			//to occur has the app, so lets change the callers status here...
			this.receiver.changeCallerHasAppStatus(true);
			//since we know that this caller has the app, we are going to delete any "NO_APP" missed call logs for him
			this.receiver.deleteNoAppLogsByPhoneNumber();
		}
	}

}
