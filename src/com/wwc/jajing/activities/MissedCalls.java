package com.wwc.jajing.activities;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.wwc.jajing.R;
import com.wwc.jajing.domain.entity.MissedCall;
import com.wwc.jajing.domain.entity.User;
import com.wwc.jajing.domain.services.CallManager;
import com.wwc.jajing.domain.value.PhoneNumber;
import com.wwc.jajing.system.JJSystem;
import com.wwc.jajing.system.JJSystemImpl;
import com.wwc.jajing.system.JJSystemImpl.Services;
import com.wwc.jajing.util.DateHelper;

public class MissedCalls extends Activity implements OnClickListener{

	private static final String TAG = "MissedCalls";
	
	private CallManager callManager;
	private User user;
	
	private TableLayout tl;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_missed_calls);
		
		//CAHCHE SYSTEM FOR PERFORMANCE
		JJSystem jjSystem = JJSystemImpl.getInstance();
		this.user = (User) jjSystem.getSystemService(Services.USER);
		this.callManager = (CallManager) jjSystem.getSystemService(Services.CALL_MANAGER);
		
		tl = (TableLayout) findViewById(R.id.tableMissedCalls);
		
	
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.d(TAG, MissedCall.Actions.HUNG_UP.toString());
		Intent intentThatCausedStart = this.getIntent();
		if(this.receivedListOfRecentCallsFlag()) {
			List<MissedCall> mcList = this.callManager.getRecentMissedCallLog().getRecentCalls();
			Log.d(TAG, "Populating list with:"+ mcList.size() + " number of recent calls");
			this.populateTable(mcList);
			//now clear the recent call list
			this.callManager.getRecentMissedCallLog().clearRecentMissedCalls();
			Log.d(TAG, "Recent calls cleared from missed call log.");

		} else {
			//populate our table with missed calls
			List<MissedCall> missedCalls = MissedCall.find(MissedCall.class, "action=? or action=? or action=? or action=? or action=?", new String[] {MissedCall.Actions.HUNG_UP.toString(),MissedCall.Actions.LEFT_VOICEMAIL.toString(), MissedCall.Actions.WILL_CALL_BACK.toString(),MissedCall.Actions.MISSED_CALL.toString() , MissedCall.Actions.NO_APP.toString()}, "", "time","");
			this.populateTable(missedCalls);
			Log.d(TAG, "Finished populating table");
		}
			
		
		
	}
	
	private Boolean receivedListOfRecentCallsFlag()
	{
		if(getIntent().getBooleanExtra("recentFlag", false) == true)
		{
			return true;
		} else {
			return false;
		}
	}
	
	
	private void populateTable(List<MissedCall> missedCalls)
	{
		if(missedCalls == null) {
			throw new NullPointerException("Missed Call List is NULL");
		}
		if(missedCalls.size() == 0) {
			TextView tv = new TextView(this);
			tv.setText("You do not have any missed calls at this time.");
			tv.setPadding(10, 10, 10, 10);
			tv.setTextColor(Color.WHITE);
			setContentView(tv);
		}
		Log.d(TAG, "Missed call list size:"+ missedCalls.size());

		for(MissedCall missedCall : missedCalls) {
			
			//create a table row
			TableRow tr = new TableRow(this);
			
			
			//create test view for number
			TextView tvNumber = new TextView(this);
			tvNumber.setTag(missedCall.phoneNumber().toString());
			tvNumber.setOnClickListener(this);
			tvNumber.setText(missedCall.getContactName(this));
			tvNumber.setTextColor(Color.parseColor("#ffffbb33"));
			tvNumber.setPadding(10, 10, 10, 20);
			Log.d(TAG, missedCall.phoneNumber().toString());
			
			
			
			//create text view for time
			TextView tvTime = new TextView(this);
			Log.d(TAG, DateHelper.dateToString(missedCall.occuredOn()));
			//TODO - maybe rewrite the to string method here, for no we will use substring to format seconds out
			tvTime.setText(DateHelper.dateToString(missedCall.occuredOn()).trim().substring(0, 19) +DateHelper.dateToString(missedCall.occuredOn()).trim().substring(19, 21).toLowerCase() );
			tvTime.setTextColor(Color.WHITE);
			tvTime.setPadding(10, 10, 10, 10);

			//create text view for action taken 
			TextView tvAction = new TextView(this);
			String actionTakenBbyCaller = missedCall.getActionTakenByCaller().getName().substring(0,1).toUpperCase() + missedCall.getActionTakenByCaller().getName().substring(1).toLowerCase();
			if(actionTakenBbyCaller.equalsIgnoreCase("No app")) actionTakenBbyCaller = "";
			tvAction.setText(actionTakenBbyCaller );
			tvAction.setTextColor(Color.WHITE);
			tvAction.setPadding(20, 10, 10, 20);


			//add the view to tthe table row
			tr.addView(tvNumber);
			tr.addView(tvTime);
			tr.addView(tvAction);

			//add table row to table layout
			tl.addView(tr);
		}
	}

	@Override
	public void onClick(View view) {
		//make call
		this.user.makeCall(new PhoneNumber(view.getTag().toString()));
		this.user.setIsMakingCall(false);
	}



}
