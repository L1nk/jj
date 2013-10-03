package com.wwc.jajing.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.wwc.jajing.R;
import com.wwc.jajing.domain.entity.User;
import com.wwc.jajing.sms.JJSMS;
import com.wwc.jajing.system.JJSystemImpl;
import com.wwc.jajing.system.JJSystemImpl.Services;

public class AboutToBreakThrough extends Activity{
	
	private JJSMS aJJSMS;
	
	private static final String TAG = "AboutToBreakThrough";
	private User user = (User) JJSystemImpl.getInstance().getSystemService(Services.USER);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		  Intent i = getIntent();
		  this.aJJSMS = (JJSMS) i.getSerializableExtra("jjsms");
	
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("You are about to break through, continue?");
		builder.setTitle("Break Through");
		
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	              // User clicked OK button
	        	// we were give a response saying its okay to call, so make the call
	        	   AboutToBreakThrough.this.user.makeCall(AboutToBreakThrough.this.aJJSMS.getSendersPhoneNumber());
	        	//after we make the call we need to reset flag
	        	 AboutToBreakThrough.this.user.setIsMakingCall(false);
	       		Log.d(TAG,"we got a response saying its okay to call, because CAll-RECEIVER allowed it!");
	       			
	           }
	           
	       });
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	               // User clicked Cancel button
	        	   
	        	 //after we make the cancel to make call we need to reset flag
	        	  AboutToBreakThrough.this.user.setIsMakingCall(false);
	        	  AboutToBreakThrough.this.finish();
	       			
	           }
	       });
		
		
		
		AlertDialog dialog = builder.create();
		dialog.show();
		
	}

	@Override
	protected void onStop() {
		super.onStop();
		this.finish();
	}
}
