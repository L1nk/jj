package com.wwc.jajing.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.wwc.jajing.R;
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_away);

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
	
}
