package com.wwc.jajing.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.wwc.jajing.settings.time.TimeSettingTaskManager;

public class TimeSettingReceiver extends BroadcastReceiver{

	public static final String EXTRA_TIME_SETTING_ID = "timeSettingId";
	
	private static final String TAG = "TimeSettingReceiver";
	@Override
	public void onReceive(Context context, Intent intent) {
		
		//do this when we turn time setting on
		if(intent.getAction().equalsIgnoreCase("com.exmaple.jajingprototype.system.event.TIME_SETTING_ON")) 
		{
			Long settingId = intent.getLongExtra(EXTRA_TIME_SETTING_ID, -1);
			
			TimeSettingTaskManager.getInstance().turnTimeSettingOn(settingId);
			
			
		//do this when we turn time setting off
		} else if (intent.getAction().equalsIgnoreCase("com.exmaple.jajingprototype.system.event.TIME_SETTING_OFF")) {
			
			Long settingId = intent.getLongExtra(EXTRA_TIME_SETTING_ID, -1);
			
			TimeSettingTaskManager.getInstance().turnTimeSettingOff(settingId);
			
			Log.d(TAG, "time setting intent to turn off time setting was received.");


		}
		
	}
	
	

}
