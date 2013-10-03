package com.wwc.jajing.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.wwc.jajing.R;
import com.wwc.jajing.domain.entity.User;
import com.wwc.jajing.sms.JJSMS;
import com.wwc.jajing.sms.JJSMSManager;
import com.wwc.jajing.system.JJSystemImpl;
import com.wwc.jajing.system.JJSystemImpl.Services;

public class DoNotDisturbOptions extends Activity {

	private static final String TAG = "DoNotDisturbOptions";
	private User user;
	private JJSMS jjsms;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_options_to_calling_party_do_not_disturb);

		this.user = (User) JJSystemImpl.getInstance().getSystemService(
				Services.USER);

	}

	@Override
	protected void onStart() {
		super.onStart();

		this.jjsms = (JJSMS) getIntent().getSerializableExtra(
				OptionsToCallingParty.JJSMS);

	}

	@Override
	protected void onStop() {
		super.onStop();
		this.finish();
	}

	public void leaveVoiceMail(View view) {
		PleaseWait.showPleaseWaitDialog(this, "Please Wait", "sending you to voicemail...");
		// let call-receiver know that this caller wants to leave voicemail
		JJSMSManager jjsmsManager = (JJSMSManager) JJSystemImpl.getInstance()
				.getSystemService(Services.SMS_MANAGER);
		jjsmsManager.getMessenger().sendRawSms(
				new JJSMS("#detach/LEAVE_VOICEMAIL"),
				this.jjsms.getSendersPhoneNumber().toString());
		Log.d(TAG, this.jjsms.getSendersPhoneNumber().toString());
		this.finish();

	}
	
	public void hangUp(View view) {
		// let the call receiver know that this caller wants to hang up
		JJSMSManager jjsmsManager = (JJSMSManager) JJSystemImpl.getInstance()
				.getSystemService(Services.SMS_MANAGER);
		jjsmsManager.getMessenger().sendRawSms(new JJSMS("#detach/HANG_UP"),
				this.jjsms.getSendersPhoneNumber().toString());
		Log.d(TAG, this.jjsms.getSendersPhoneNumber().toString());
		this.finish();

	}

	public void willCallBack(View view) {
		// let the call receiver know that this caller wants to hang up
		JJSMSManager jjsmsManager = (JJSMSManager) JJSystemImpl.getInstance()
				.getSystemService(Services.SMS_MANAGER);
		jjsmsManager.getMessenger().sendRawSms(new JJSMS("#detach/WILL_CALL_BACK"),
				this.jjsms.getSendersPhoneNumber().toString());
		Log.d(TAG, this.jjsms.getSendersPhoneNumber().toString());
		this.finish();
	}
	
	
	public void leaveTextMessage(View view)
	{
		Intent smsIntent = new Intent(Intent.ACTION_VIEW);
		smsIntent.putExtra("address", this.jjsms.getSendersPhoneNumber().toString());
		smsIntent.setData(Uri.parse("smsto:" + this.jjsms.getSendersPhoneNumber().toString()));
		startActivity(smsIntent);

	}
	
	
	

	
	
	
	

}
