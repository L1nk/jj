package com.wwc.jajing.sms.receiver;

import android.content.Context;
import android.content.Intent;

import com.wwc.jajing.activities.AboutToBreakThrough;
import com.wwc.jajing.activities.PlainAlertDialog;
import com.wwc.jajing.activities.WantsToDisturb;
import com.wwc.jajing.activities.callbacks.onUserCallReceiveingDoesNotWantToBeDisturbed;
import com.wwc.jajing.domain.entity.User;
import com.wwc.jajing.sms.JJSMS;
import com.wwc.jajing.system.JJSystemImpl;
import com.wwc.jajing.system.JJSystemImpl.Services;

public class DisturbReceiver implements CommandReceiver {

	private static final String TAG = "WantsToDisturbReceiver";
	private Context context;
	private JJSMS jjsms;

	private User user = (User) JJSystemImpl.getInstance().getSystemService(
			Services.USER);

	public DisturbReceiver(Context aContext, JJSMS aJJSMS) {
		this.context = aContext;
		this.jjsms = aJJSMS;

	}

	public void showResponseDialog() {
		if (this.jjsms.getFromExtras("ALLOW").toString()
				.equalsIgnoreCase("TRUE")) {
			
			//ask user if he wants to break through
			Intent i = new Intent(context, AboutToBreakThrough.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			i.putExtra("jjsms", this.jjsms);
			context.startActivity(i);
		} else {

			PlainAlertDialog.alertUser(context, "Sorry", this.jjsms
					.getSendersPhoneNumber().toString()
					+ " does not want to be disturbed at this time.",
					new onUserCallReceiveingDoesNotWantToBeDisturbed(), false);
		}

	}

	public void showWantsToDisturbActivity() {
		Intent i = new Intent(context, WantsToDisturb.class);
		i.putExtra(WantsToDisturb.JJSMS, this.jjsms);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(i);
		
		//fix bug here. we know we are possible expecting a call, so set isMakingCall to false
		this.user.setIsMakingCall(false);
	}

	public boolean isResponse() {

		return this.jjsms.isResponse();
	}

}
