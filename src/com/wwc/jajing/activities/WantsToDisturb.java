package com.wwc.jajing.activities;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.wwc.jajing.R;
import com.wwc.jajing.domain.entity.Caller;
import com.wwc.jajing.domain.entity.CallerImpl;
import com.wwc.jajing.domain.entity.User;
import com.wwc.jajing.permissions.PermissionManager;
import com.wwc.jajing.sms.JJSMS;
import com.wwc.jajing.sms.JJSMSManager;
import com.wwc.jajing.sms.JJSMSMessenger;
import com.wwc.jajing.sms.JJSMSSenderPhoneNumber;
import com.wwc.jajing.system.JJSystem;
import com.wwc.jajing.system.JJSystemImpl;
import com.wwc.jajing.system.JJSystemImpl.Services;

public class WantsToDisturb extends Activity {

	private static final String TAG = "WantsToDisturb";
	// we expect the JJSMS
	public static final String JJSMS = "JJSMS";

	private TextView callersPhoneNumber;
	private JJSMSSenderPhoneNumber callersPNumber;

	private User user;
	private JJSMSManager jjsmsManager;
	private JJSMSMessenger jjsmsMessenger;
	private JJSMS jjsms;
	
	private Caller currentCaller;

	/*
	 * Called only ONCE
	 * 
	 * GOOD PLACE TO PUT ANY INITIAL WORK
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wants_to_disturb);

		//CACHE SYSTEM 
		JJSystem jjSystem = JJSystemImpl.getInstance();
		user = (User) jjSystem.getSystemService(Services.USER);
		jjsmsManager = (JJSMSManager) jjSystem.getSystemService(Services.SMS_MANAGER);
		jjsmsMessenger = jjsmsManager.getMessenger();
		
		// Init our views
		callersPhoneNumber = (TextView) findViewById(R.id.textCallersPhoneNumber);

	}

	@Override
	protected void onStart() {
		super.onStart();

		Intent intent = getIntent();
		this.jjsms = (JJSMS) intent.getSerializableExtra(this.JJSMS);
		this.callersPNumber = (JJSMSSenderPhoneNumber) jjsms
				.getSendersPhoneNumber();

		Log.d(TAG, jjsms.getRawJJSMS());

		callersPhoneNumber.setText(this.jjsms.getSendersPhoneNumber()
				.toString() + " \n is calling and wants to disturb you");

	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	public void allowCallerToDisturbHandler(View view)  // do this when we allow caller to disturb												
	{

		currentCaller = CallerImpl.getCallerByPhoneNumber(this.callersPNumber.toString());
		Log.d(TAG, "Found a caller with id:" + currentCaller.getId());
		// now give caller the permission to call
		this.user.givePermission(CallerImpl.getCallerByPhoneNumber(this.callersPNumber.toString()), PermissionManager.Permissions.SEND_CALL);
		Log.d(TAG, "Finished giving caller permissions");
		
		// now send a JJSMS response for DISTURB
		this.jjsmsMessenger.sendRawSms(new JJSMS("#detach/DISTURB?ISRESPONSE=TRUE,ALLOW=TRUE"), this.callersPNumber.toString());

		Log.d(TAG, "Call receiving has chose to ALLLOW the caller to disturb!JJSMS Response sent.");

		this.finish();
	}
	

	public void denyCallerToDisturbHandler(View view)
	{

		this.jjsmsMessenger.sendRawSms(new JJSMS("#detach/DISTURB?ISRESPONSE=TRUE,ALLOW=FALSE"), this.callersPNumber.toString());
		Log.d(TAG, "Call receiving has chose to DENY the caller to disturb!JJSMS Response sent.");

		this.finish();

	}

}
