package com.wwc.jajing.activities;

import java.io.IOException;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.wwc.jajing.R;
import com.wwc.jajing.cloud.contacts.CloudBackendAsync;
import com.wwc.jajing.cloud.contacts.CloudCallbackHandler;
import com.wwc.jajing.domain.entity.TimeSetting;
import com.wwc.jajing.domain.entity.User;
import com.wwc.jajing.settings.time.TimeSettingTaskManager;
import com.wwc.jajing.system.JJSystemImpl;
import com.wwc.jajing.system.JJSystemImpl.Services;

/*
 * This is the screen shown when the user is Away.
 */

@SuppressLint("DefaultLocale")
public class AwayActivity extends Activity {

	private static final String TAG = "AwayActivity";
	public static final String AVAILABILITY_STATUS = "AVAILABILITY_STATUS";
	public static final String EXTRA_KEY_TIME_SETTING_ID = "time_setting_id";
	

	// The intent that is broadcasted when the user is AWAY
	public static final String AWAY_INTENT = "com.exmaple.jajingprototype.intent.action.AWAY";
	
	User user;
	TimeSetting timeSetting;

	TextView awayStatus;
	TextView availabilityTime;
	TextView notification;
	
	CloudBackendAsync m_cloudAsync ;
	Context mContext ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_away);

		mContext = getApplicationContext() ;
		
		m_cloudAsync = new CloudBackendAsync(mContext);
		
		user = (User) JJSystemImpl.getInstance()
				.getSystemService(Services.USER);
		// timeSetting = TimeSetting.findById(TimeSetting.class,
		// this.getIntent().getLongExtra(EXTRA_KEY_TIME_SETTING_ID, 1L));
		Log.d(TAG, TimeSettingTaskManager.getInstance()
				.getTimeSettingIdClosestToBeingDone().toString());
		timeSetting = TimeSetting.findById(TimeSetting.class,
				TimeSettingTaskManager.getInstance()
						.getTimeSettingIdClosestToBeingDone());
		awayStatus = (TextView) findViewById(R.id.textStatus);

		availabilityTime = (TextView) findViewById(R.id.textAvailabilityTime);
		notification = (TextView) findViewById(R.id.textNotification);

		awayStatus.setText(user.getUserStatus().getAvailabilityStatus());

		this.updateStatusOnCloud( user );
	}

	@Override
	protected void onStart() {
		super.onStart();
		
		if(this.user.isAvailable()){//if the user is available make him go unavailable
			availabilityTime.setText("");
			notification.setText("");

		} else {
			availabilityTime.setText("(some callers may disturb you during this time)");
			if(timeSetting.getEndTime().equalsIgnoreCase("") || timeSetting.getStartTime().equalsIgnoreCase("") || TimeSetting.hasEndTimePassed(timeSetting.getEndTime())) {
				notification.setVisibility(View.INVISIBLE);
			} else {
				notification.setVisibility(View.VISIBLE);
				notification.setText("Your status will take affect at " + timeSetting.getStartTime().toLowerCase() +" and change to available after \n" + timeSetting.getEndTime().toLowerCase() + " " + this.user.getAvailabilityTime().getTimeZoneString().toLowerCase());

			}

		}
	

	}
	
	public void continueHome(View view){
		Intent i = new Intent(this, MainActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		this.startActivity(i);
	}
	
	/**
	 * Asynchronously loads detached user contacts from cloud
	 * 
	 */
	private void updateStatusOnCloud( User user ) {

		CloudCallbackHandler<JSONObject> handler = new CloudCallbackHandler<JSONObject>() {
			@Override
			public void onComplete( JSONObject results ) {
				Toast.makeText( mContext , "Status pushed to Cloud successfully", Toast.LENGTH_LONG ).show();
			}
			@Override
			public void onError( IOException exception ) {
				CloudBackendAsync.handleEndpointException( exception );
			}
		};
		//Change userId to actual user phone number
		long userId = 1l ;
		SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences( getApplicationContext() );
		userId = shared.getLong( "id" , 1l ) ;
		if( userId == 1l ) {
			Toast.makeText( this, "Not valid phone number for API calls", Toast.LENGTH_LONG ).show();
		}
		m_cloudAsync.pushStatusToCloud( userId , user , handler );
	}
	
}
