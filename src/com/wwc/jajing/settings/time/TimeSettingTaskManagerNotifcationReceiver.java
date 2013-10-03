package com.wwc.jajing.settings.time;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.wwc.jajing.activities.TimeOnEnd;
import com.wwc.jajing.domain.entity.User;
import com.wwc.jajing.services.JJOnAwayService;
import com.wwc.jajing.system.JJSystemImpl;
import com.wwc.jajing.system.JJSystemImpl.Services;

public class TimeSettingTaskManagerNotifcationReceiver extends BroadcastReceiver{

	private final static String TAG = "TimeSettingBroadcastReceiver";
		
	private User user;
			
	public TimeSettingTaskManagerNotifcationReceiver()
	{
		super();
		this.user = (User) JJSystemImpl.getInstance().getSystemService(Services.USER);
		
	}
	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equalsIgnoreCase("com.exmaple.jajingprototype.system.event.TIME_SETTING_ENDED")) {
			if(!this.user.isAvailable()) {
				Log.d(TAG, "user is not available, prompting user.");

					//prompt user to go available
					Intent i = new Intent(context, TimeOnEnd.class);
					i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(i);

				
			} else {
				this.user.goAvailable();
			}
			

		}
	}

}

