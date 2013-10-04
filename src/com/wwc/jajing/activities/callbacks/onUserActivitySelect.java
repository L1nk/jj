package com.wwc.jajing.activities.callbacks;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.wwc.jajing.domain.entity.TimeSetting;
import com.wwc.jajing.domain.entity.User;
import com.wwc.jajing.domain.value.AvailabilityTime;
import com.wwc.jajing.receivers.TimeSettingReceiver;
import com.wwc.jajing.settings.time.TimeSettingId;
import com.wwc.jajing.system.JJSystemImpl;
import com.wwc.jajing.system.JJSystemImpl.Services;
import com.wwc.jajing.util.Alert;

/*
 * When the user selects an end time on the "i'm ?" scrren, if the time he selected interferes with the time settings
 * we show him an alert dialog with this callback
 */
@SuppressWarnings("serial")
public class onUserActivitySelect implements Alert {

	private  static final String TAG = "CallBack";
	
	private ArrayList<TimeSettingId> interferingTimeSettings;
	private String endTime;
	private String unavailabilityReason;
		
	public onUserActivitySelect(ArrayList<TimeSettingId> interferingTimeSettings, String endTime, String unavailabilityReason)
	{
		this.interferingTimeSettings = interferingTimeSettings;
		this.endTime = endTime;
		this.unavailabilityReason  = unavailabilityReason;
	}
	
	@Override
	public void onAlertPositive() { //do this when user wants to turn time settings off
		
		Context context = (Context) JJSystemImpl.getInstance().getSystemService(Services.CONTEXT);
		User user = (User) JJSystemImpl.getInstance().getSystemService(Services.USER);


		for(TimeSettingId aTimeSettingId : this.interferingTimeSettings) {
			TimeSetting aTimeSetting = TimeSetting.findById(TimeSetting.class, aTimeSettingId.getId());
			Intent i = new Intent().setAction("com.exmaple.jajingprototype.system.event.TIME_SETTING_OFF");
		 	i.putExtra(TimeSettingReceiver.EXTRA_TIME_SETTING_ID, aTimeSetting.getId());
		 	context.sendBroadcast(i);
		}
		
		Log.d(TAG, new AvailabilityTime(this.endTime).getAvailabilityTimeString());
		user.goUnavailable(this.unavailabilityReason, new AvailabilityTime(this.endTime));


	}

	@Override
	public void onAlertNegative() { //user decides not to turn time settings off, so force him to choose another time
		
	}

}
