package com.wwc.jajing.activities;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.Toast;

import com.wwc.jajing.R;
import com.wwc.jajing.activities.callbacks.onUserActivitySelect;
import com.wwc.jajing.domain.entity.TimeSetting;
import com.wwc.jajing.domain.entity.User;
import com.wwc.jajing.domain.value.AvailabilityTime;
import com.wwc.jajing.fragment.mTimePicker;
import com.wwc.jajing.settings.time.TimeSettingId;
import com.wwc.jajing.settings.time.TimeSettingValidator;
import com.wwc.jajing.system.JJSystemImpl;
import com.wwc.jajing.system.JJSystemImpl.Services;

/*
 * This is the screen shown when the user is going to pick an away option.
 * The option represents why he is away.
 */
public class AwayOptions extends Activity implements OnClickListener {

	private static final String TAG = "AwayOptionsMenu";
	public static final String CUSTOM_MESSAGE = "CustomMessage";

	// public static final String AWAY_STATUS = "awayStatus";
	private User user = (User) JJSystemImpl.getInstance().getSystemService(
			Services.USER);

	private String unavailabilityReason;
	private String customMessage;

	private Button driving;
	private Button family;
	private Button meeting;
	private Button crowded;
	private Button call;
	private Button custom;
	private Button message;

	private String endTime;
	private String startTime;

	private String pendingEndTime = "";
	private String pendingStartTime = "";
	
	private DialogFragment timeFrag;


	public AwayOptions()
	{
		super();
		instance = this;
	}
	
	private static AwayOptions instance;

	public static AwayOptions getInstance()
	{
		return instance;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    getWindow().setWindowAnimations(Animation.ZORDER_TOP);
		setContentView(R.layout.activity_away_options);

		// Initialize the UI componenets
		driving = (Button) findViewById(R.id.buttonDriving);
		family = (Button) findViewById(R.id.buttonFamily);
		meeting = (Button) findViewById(R.id.buttonMeeting);
		crowded = (Button) findViewById(R.id.buttonCrowded);
		call = (Button) findViewById(R.id.buttonImportantCall);
		custom = (Button) findViewById(R.id.buttonCustom);
		message = (Button) findViewById(R.id.buttonCustomMessage);

		// register click handlers
		this.registerClickHandlers();

	}

	private void registerClickHandlers() {
		call.setOnClickListener(this);
		driving.setOnClickListener(this);
		family.setOnClickListener(this);
		meeting.setOnClickListener(this);
		crowded.setOnClickListener(this);
		custom.setOnClickListener(this);
		message.setOnClickListener(this);

	}

	/*
	 * Used when user sets his custom message
	 */
	@Override
	protected void onStart() {
		super.onStart();
		// Restore preferences
		SharedPreferences settings = getSharedPreferences(CUSTOM_MESSAGE, 0);
		String customPrefs = settings.getString("custom", null);

		String customMessage = this.getIntent().getStringExtra("custom");
		if (customMessage != null) {
			this.customMessage = customMessage;
			this.saveCustomMessageToUserPref(customMessage);
			this.showCustomMessageButton(true);

		} else if (customPrefs != null) {
			this.customMessage = customPrefs;
			this.showCustomMessageButton(true);

		} else {
			this.showCustomMessageButton(false);

		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.saveCustomMessageToUserPref(this.customMessage);
		this.finish();
		
	}
	
	 @Override
	 public void onSaveInstanceState(Bundle outState) {
		 
	 }

	private void saveCustomMessageToUserPref(String aCustomMessage) {

		// We need an Editor object to make preference changes.
		// All objects are from android.context.Context
		SharedPreferences settings = getSharedPreferences(CUSTOM_MESSAGE, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("custom", aCustomMessage);

		// Commit the edits!
		editor.commit();
		Log.d(TAG, "saved custom message to user prefs.");
	}

	private void showCustomMessageButton(boolean shouldShow) {
		if (shouldShow) {
			// show the button with the message te user has set
			this.message.setText(this.customMessage);
			this.message.setVisibility(View.VISIBLE);
		} else {
			// hide the button
			this.message.setVisibility(View.GONE);

		}
	}

	/*
	 * Makes user "unavailable"
	 */
	@Override
	public void onClick(View view) {

		switch (view.getId()) {
		case R.id.buttonDriving:
			this.unavailabilityReason = "Driving";
			this.promptUserForTime(true);
			break;
		case R.id.buttonFamily:
			this.unavailabilityReason = "With Family";
			this.promptUserForTime(true);
			break;
		case R.id.buttonCrowded:
			this.unavailabilityReason = "In a Crowded Place";
			this.promptUserForTime(true);
			break;
		case R.id.buttonMeeting:
			this.unavailabilityReason = "In a Meeting";
			this.promptUserForTime(true);
			break;
		case R.id.buttonImportantCall:
			this.unavailabilityReason = "On an Important Call";
			this.promptUserForTime(true);
			break;
		case R.id.buttonCustomMessage:
			this.unavailabilityReason = this.customMessage.toUpperCase();
			this.promptUserForTime(true);
			break;
		case R.id.buttonCustom:
			Intent i = new Intent(this, CustomAvailabilityStatus.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			this.startActivity(i);
			break;
		default:
			user.goAvailable();

		}

	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void promptUserForTime(boolean isStartTime) {

		timeFrag = new mTimePicker(isStartTime);
		
		timeFrag.setRetainInstance(true);

		timeFrag.show(getFragmentManager(), "timePicker");
	}

	public void setStartTime(String aStartTime) {
		if (!this.pendingStartTime.equalsIgnoreCase(aStartTime)) {
			this.pendingStartTime = aStartTime;
			this.startTime = this.pendingStartTime;
		}
		Toast.makeText(this, "start time set to : " + aStartTime, Toast.LENGTH_SHORT).show();
		removeFragment();
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void removeFragment()
	{
		FragmentManager fragManager = this.getFragmentManager();
		FragmentTransaction ft = fragManager.beginTransaction();
		ft.remove(this.timeFrag);
		ft.commit();
		promptUserForTime(false);// prompt for end time
	}

	public void setEndTime(String anEndTime) {
		//BUG THAT SUBMITS THE CURRENT TIME AS END TIME
		if (!this.pendingEndTime.equalsIgnoreCase(anEndTime) && !anEndTime.equalsIgnoreCase("")) {
				this.pendingEndTime = anEndTime;
				this.endTime = pendingEndTime;
				Toast.makeText(this, "end time set to : " + pendingEndTime, Toast.LENGTH_SHORT).show();

		} else {
			// stop code execution, there is a BUG with android calling
			// onSetTimeTwice, this is a temporary fix.
			Toast.makeText(this, "could not set end time: " + anEndTime, Toast.LENGTH_SHORT).show();
			this.pendingEndTime = "";
			return;
		}
		
		if(!TimeSetting.isEndTimeInFuture(anEndTime)) {
			Toast.makeText(this, "end time must be in future", Toast.LENGTH_SHORT).show();
			return;
		}
		if(!TimeSetting.isValidTimeInterval(this.startTime, pendingEndTime)) {
			Toast.makeText(this, "invalid interval", Toast.LENGTH_SHORT).show();
			return;
		}
		

		

		ArrayList<TimeSetting> interferingTimeSettings = TimeSettingValidator
				.getTimeSettingsThisEndTimeInterferesWith(anEndTime);
		// POSSIBLE BUG HERE... should not compare size, was used as a temp fix
		if (interferingTimeSettings.size() < 1) {// check to make sure its set
													// for the future
			boolean success = goUnavailable();
			if (success) {
				navigateToAway(1L);
			} else {
				
			}
		} else {
			PlainAlertDialog
					.alertUser(
							this,
							"Sorry",
							"This time interferes with your time setting(s), turn them off?",
							new onUserActivitySelect(
									getInterferingTimeSettingIds(interferingTimeSettings),
									this.endTime, this.unavailabilityReason),
							true);
		}

	}

	private boolean goUnavailable() {
		return this.user.goUnavailable(this.unavailabilityReason,
				this.startTime, new AvailabilityTime(this.endTime));

	}

	private void navigateToAway(Long timeSettingId) {
		Log.d(TAG,
				"availability time is now:"
						+ new AvailabilityTime(this.endTime)
								.getAvailabilityTimeString());
		// Now we can route the user to the "Away" activity.
		Intent i = new Intent(this, AwayActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		i.putExtra(AwayActivity.EXTRA_KEY_TIME_SETTING_ID, timeSettingId);
		startActivity(i);
	}

	private ArrayList<TimeSettingId> getInterferingTimeSettingIds(
			ArrayList<TimeSetting> interferingTimeSettings) {

		ArrayList<TimeSettingId> listOfTimeSettingIds = new ArrayList<TimeSettingId>();
		for (TimeSetting aTimeSetting : interferingTimeSettings) {
			listOfTimeSettingIds.add(new TimeSettingId(aTimeSetting.getId()));
		}
		return listOfTimeSettingIds;
	}
	
	

	

}
