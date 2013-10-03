package com.wwc.jajing.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.wwc.jajing.R;
import com.wwc.jajing.sms.JJSMS;
import com.wwc.jajing.sms.JJSMSManager;
import com.wwc.jajing.sms.JJSMSMessenger;
import com.wwc.jajing.sms.JJSMSSenderPhoneNumber;
import com.wwc.jajing.system.JJSystemImpl;
import com.wwc.jajing.system.JJSystemImpl.Services;

public class OptionsToCallingParty extends Activity {


	private static final String TAG = "OptionsToCallingParty";
	// we expect the JJSMS
	public static final String JJSMS = "JJSMS";
	// we expect the reason why the call-receiver is not available to answer
	// phone
	public static final String REASON = "AVAILABILITYSTATUS";
	public static final String TIME_BACK = "AVAILABILITYTIME";


	private TextView callReceivingPhoneNumberView;
	private TextView callReveingReasonBeingBusyView;
	private Button doNotDisturbButton;

	private JJSMSSenderPhoneNumber callReceivingPhoneNumber;
	private String callReceivingReasonForBeingUnavailable;
	private String callReceivingTimeBack;

	
	
	private JJSMSManager jjsmsManger = (JJSMSManager) JJSystemImpl.getInstance().getSystemService(Services.SMS_MANAGER);
	private JJSMSMessenger jjsmsMessenger = jjsmsManger.getMessenger();
	private JJSMS jjsms;

	/*
	 * Called only ONCE
	 * 
	 * GOOD PLACE TO PUT ANY INITIAL WORK
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_options_to_calling_party);

		// Init our views
		callReceivingPhoneNumberView = (TextView) findViewById(R.id.txtCallingPhoneNumber);
		callReveingReasonBeingBusyView = (TextView) findViewById(R.id.textCallingDescription);

	}

	@Override
	protected void onStart() {
		super.onStart();

		Intent intent = getIntent();
		this.jjsms = (JJSMS) intent.getSerializableExtra(this.JJSMS);
		this.callReceivingPhoneNumber = (JJSMSSenderPhoneNumber) jjsms.getSendersPhoneNumber();
		
		Log.d(TAG, "The callRecevingPhoneNumber is:" + callReceivingPhoneNumber);
		Log.d(TAG, "The availabilityStatus is:" + jjsms.getFromExtras(this.REASON));
		Log.d(TAG, jjsms.getRawJJSMS());
		
		this.callReceivingReasonForBeingUnavailable = (String) jjsms
				.getFromExtras(this.REASON);
		
		this.callReceivingTimeBack = (String) jjsms
				.getFromExtras(this.TIME_BACK);

		this.injectText();

	}
	
	@Override
	protected void onStop() {
		super.onStop();
		this.finish();
	}

	private void injectText() {
		callReceivingPhoneNumberView.setText(callReceivingPhoneNumber
				.toString());
		callReveingReasonBeingBusyView.setText("The person you are calling is \""
				+ this.callReceivingReasonForBeingUnavailable
				+ "\" and is Unavailable  until " + this.callReceivingTimeBack.toLowerCase().substring(0, 12));

	}
	
	public void requestToDisturbHandler(View view)
	{
		//to keep track of the request have to attach a requestid
		String jjsmsRequestStr = "#detach/DISTURB?";
		this.jjsmsMessenger.sendRawSms(new JJSMS(jjsmsRequestStr), this.jjsms.getSendersPhoneNumber().toString());
		this.finish();
		Log.d(TAG, "request to disturb sent.");
		this.showProgressDialog();
	}
	
	private void showProgressDialog()
	{
		Intent i = new Intent(this, PleaseWait.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.putExtra("title", "Connecting Call....");
		i.putExtra("description", "please wait while detach connects your call...");

		
		this.startActivity(i);
	}

	/*
	 * 
	 * Takes to the user to DoNotDisturbOptions Activity, and passes the JJSMS
	 * object with it.
	 * 
	 * The DoNotDisturbOptions Activity is shown when the user hits "NO" on the
	 * OptionsToCallingParty Activity
	 */
	public void showDoNotDisturbOptions(View view) {
		Intent i = new Intent(this, DoNotDisturbOptions.class);
		i.putExtra(JJSMS, this.jjsms);
		startActivity(i);
	}

}
