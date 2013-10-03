package com.wwc.jajing.settings.time;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import android.util.Log;

public class DailyNotifier {

	private static final String TAG = "DailyNotifier";
	private ArrayList<onDayChangeListner> listeners;
	
	private static DailyNotifier instance = new DailyNotifier();
	
	private DailyNotifier()
	{
		listeners = new ArrayList<onDayChangeListner>();
		//run a task every 24 hours notifying listeners
		this.run24HoursTaskNotification();
		
		
		Log.d(TAG, "Notifier created.");
		
	}
	
	
	private void run24HoursTaskNotification()
	{
		  Timer timer = new Timer();
		  Calendar startingTime = Calendar.getInstance(TimeZone.getDefault());
		 
		  // Starting at 12:00 AM in every day the Morning
		  startingTime.set(Calendar.HOUR_OF_DAY, 12);
		  startingTime.set(Calendar.MINUTE, 00);
		  startingTime.set(Calendar.SECOND, 00);
		  
		  timer.schedule(new m24HourTask() , 0, 1000 * 60 * 60 * 24);
		 
	}
	
	public static final DailyNotifier getInstance()
	{
		return instance;
	}
	
	
	
	
	public void registerDailyNotifiee(onDayChangeListner aListener)
	{
		this.listeners.add(aListener);
	}
	
	public void unregisterDailyNotifiee(onDayChangeListner aListener)
	{
		this.listeners.remove(aListener);
	}
	
	public void notifyDayChange()
	{
		Log.d(TAG, "we are notifying " + this.listeners.size() + " components of the day change change." );
		for (onDayChangeListner aListener : this.listeners) 
		{
			aListener.onDayChange();
		}
	}
	
	
	private class m24HourTask extends TimerTask
	{

		@Override
		public void run() {
			DailyNotifier.getInstance().notifyDayChange();
			
			Log.d(TAG, "24 hours have passed.");
		}
		
	}
	
	
	
}
