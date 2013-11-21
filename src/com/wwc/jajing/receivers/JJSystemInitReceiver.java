package com.wwc.jajing.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.wwc.jajing.domain.entity.TimeSetting;
import com.wwc.jajing.domain.entity.TimeSetting.Days;
import com.wwc.jajing.settings.time.DailyNotifier;
import com.wwc.jajing.settings.time.TimeSettingTaskManager;

/*
 * Work that needs to be done after system is done initializing.
 * 
 */
public class JJSystemInitReceiver extends BroadcastReceiver {

	private static final String TAG  = "JJSystemInitReceiver";
	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equalsIgnoreCase("com.exmaple.jajingprototype.system.event.INIT_COMPLETE"))
		{
			Log.d(TAG, "SYSTEM INIT_COMPLETE BROADCAST INTENT RECEIVED!");
			//after the system is done initilaizeing we can do some light secondary work here...
			

			//Create our MAIN time setting
			if(TimeSetting.findById(TimeSetting.class, 1L) == null) {
				Days[] allDays = new Days[] {Days.SUNDAY, Days.MONDAY, Days.TUESDAY, Days.WEDNESDAY, Days.THURSDAY, Days.FRIDAY, Days.SATURDAY};
				TimeSetting main = new TimeSetting(context,"", "", allDays, "Available");
				main.save();
			}
			
			
			//lets register our componenets that need to be notified of somehting every 24 hours...
			DailyNotifier dn = DailyNotifier.getInstance();
			dn.registerDailyNotifiee(TimeSettingTaskManager.getInstance());
			
		}
		
	}

}
