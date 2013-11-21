package com.wwc.jajing.settings.time;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.util.Log;

import com.wwc.jajing.domain.entity.TimeSetting;
import com.wwc.jajing.domain.entity.User;
import com.wwc.jajing.domain.value.AvailabilityTime;
import com.wwc.jajing.system.JJSystem;
import com.wwc.jajing.system.JJSystemImpl;
import com.wwc.jajing.system.JJSystemImpl.Services;

/*
 * responsible for managing all time setting tasks that will occur or have ended.
 * 
 * will be notified initially of day chnage on system boot
 * 
 * 
 */
public class TimeSettingTaskManager implements onDayChangeListner {

	private static final String TAG = "TimeSettingTaskManager";

	// we need to keep a collection of the tasks we have issued on particular
	// time settings
	// so we can cancel them before they get run if we want to
	private final HashMap<Long, TimerTask> timeSettingStartTasks = new HashMap<Long, TimerTask>();
	private final HashMap<Long, TimerTask> timeSettingEndTasks = new HashMap<Long, TimerTask>();

	private final HashMap<Long, TimeSetting> listOfTimeSettingsThatAreOn = new HashMap<Long, TimeSetting>();

	private static TimeSettingTaskManager instance;

	public final Timer aTimer = new Timer();

	private Context context;
	private User user;
	
	private TimeSettingTaskManager() {
		//CACHE SYSTEM 
		JJSystem jjSystem = JJSystemImpl.getInstance();
		
		user =  (User) jjSystem.getSystemService(Services.USER);
		context = (Context) jjSystem.getSystemService(Services.CONTEXT);
	}
	
	public Long getTimeSettingIdClosestToBeingDone()
	{
		TimeSetting result = null;
		for(TimeSetting aTimeSetting : listOfTimeSettingsThatAreOn.values()) {
			if(result == null) {
				result = aTimeSetting;
			}
			Long endTime = aTimeSetting.getEndTimeAsDate().getTime();
			if(endTime <= result.getEndTimeAsDate().getTime()) {
				result = aTimeSetting;
			}
			
		}
		if (result == null) return 1L;
		return result.getId();
	}
	

	public void turnTimeSettingOn(Long timeSettingId) {
		// we need to turn the time setting on
		TimeSetting aTimeSetting = TimeSetting.findById(TimeSetting.class,
				timeSettingId);
		aTimeSetting.on();
		aTimeSetting.save();
		
		// now we need to register this time setting as ON with our task mananger
		this.registerTimeSettingAsOn(aTimeSetting);
		// assign time setting a task to execute
		this.assignTaskToTimeSetting(aTimeSetting);
	}

	public void turnTimeSettingOff(Long timeSettingId) {
		// we need to turn the time setting off
		TimeSetting aTimeSetting = TimeSetting.findById(TimeSetting.class,timeSettingId);
		aTimeSetting.off();
		aTimeSetting.save();
		// lets cancel time setting
		this.cancelTimeSetting(aTimeSetting);
		this.unregisterTimeSettingAsOn(aTimeSetting);

	}

	private void cancelTimeSetting(TimeSetting aTimeSetting) {
		this.cancelStartTask(aTimeSetting);
		this.cancelEndTask(aTimeSetting);
		aTimer.purge();
	}

	public static final TimeSettingTaskManager getInstance() {
		if (instance == null) {
			instance = new TimeSettingTaskManager();
			return instance;
		}
		return instance;
	}

	private void cancelStartTask(TimeSetting aTimeSetting) {
		if (this.timeSettingStartTasks.get(aTimeSetting.getId()) != null) {
			this.timeSettingStartTasks.get(aTimeSetting.getId()).cancel();
			this.timeSettingStartTasks.remove(aTimeSetting.getId());

			Log.d(TAG,
					"removed start task from time setting tasks. Size is now:"
							+ this.timeSettingStartTasks.size());
		} else {
			Log.d(TAG,
					"could not remove start task from time setting tasks. Size is now:"
							+ this.timeSettingStartTasks.size());

		}
	}

	private void cancelEndTask(TimeSetting aTimeSetting) {
		if (this.timeSettingEndTasks.get(aTimeSetting.getId()) != null) {
			this.timeSettingEndTasks.get(aTimeSetting.getId()).cancel();
			//this.timeSettingEndTasks.remove(aTimeSetting.getId());

			Log.d(TAG, "removed end task from time setting tasks. Size is now:"
					+ this.timeSettingEndTasks.size());
		} else {
			Log.d(TAG,
					"could not remove end task from time setting tasks. Size is now:"
							+ this.timeSettingEndTasks.size());

		}
	}

	private void registerTimeSettingAsOn(TimeSetting aTimeSetting) {

		this.listOfTimeSettingsThatAreOn
				.put(aTimeSetting.getId(), aTimeSetting);
		Log.d(TAG, "registering time settingId:" + aTimeSetting.getId());

	}

	private void unregisterTimeSettingAsOn(TimeSetting aTimeSetting) {
		this.listOfTimeSettingsThatAreOn.remove(aTimeSetting.getId());
		Log.d(TAG, "unregistering time settingId:" + aTimeSetting.getId());

	}

	/*
	 * Will be called initially on boot
	 */
	@Override
	public void onDayChange() {

		// update time setting "ON" list
		this.updateListOfTimeSettingsThatAreOn();
		// when the day changes loop through the list of time settings and
		// figure out which ones apply today
		List<TimeSetting> listOfTimeSettingThatApplyToday = this
				.getListOfTimeSettingsThatApplyToday();
		// the time settings that apply today should be assignd the task of
		// turning jajjing on/off at appropriate times
		this.assignTaskToEachTimeSetting(listOfTimeSettingThatApplyToday);

		Log.d(TAG,
				"onDayChange for TimeSettingTaskManager is finished running ");

	}

	private void assignTaskToEachTimeSetting(
			List<TimeSetting> aListOfTimeSettingsThatApplyToday) {
		for (TimeSetting aTimeSetting : aListOfTimeSettingsThatApplyToday) {

			this.assignTaskToTimeSetting(aTimeSetting);
		}
	}

	public void assignTaskToTimeSetting(TimeSetting aTimeSetting) {
		if (aTimeSetting.hasTimeSettingForToday()) {
			// check if time setting's end time has already passed
			if (!aTimeSetting.hasEndTimePassed()) {
				// check to see if start time has passed
				if (aTimeSetting.hasStartTimePassed()) {// do this when the time
														// setting should be
														// applied immediately
					// we need the user to go unavailable immediately
					String status = this.user.getUserStatus().getAvailabilityStatus();
					if(status.equalsIgnoreCase("AVAILABLE")) status = aTimeSetting.status;
					this.user.goUnavailable(status, new AvailabilityTime(
							aTimeSetting.getEndTime()));
					// then we need to commit a new start task immediately
					this.scheduelNewStartTaskToRunNow(aTimeSetting);
					Log.d(TAG,
							"start has passed, but end time hasn't....scheduling a start task to run now.");

				} else {// do this, when time setting will apply in the future
						// today
					this.scheduelNewStartTaskForTimeSettingInFutureLaterToday(aTimeSetting);
					Log.d(TAG,
							"start time has not passed....scheduling a start task for the future.");
				}

			} else {
				Log.d(TAG, "time setting's end time has already passed.");
			}
		} else {
			Log.d(TAG, "time setting does not apply today.");

		}

	}

	private List<TimeSetting> getListOfTimeSettingsThatApplyToday() {
		ArrayList<TimeSetting> aListOfTimeSettingsThatApplyToday = new ArrayList<TimeSetting>();
		for (TimeSetting aTimeSetting : this.listOfTimeSettingsThatAreOn
				.values()) {
			if (aTimeSetting.hasTimeSettingForToday()) {
				aListOfTimeSettingsThatApplyToday.add(aTimeSetting);
			}
		}
		return aListOfTimeSettingsThatApplyToday;
	}

	private void updateListOfTimeSettingsThatAreOn() {
		List<TimeSetting> aTimeSettingList = TimeSetting
				.listAll(TimeSetting.class);
		for (TimeSetting aTimeSetting : aTimeSettingList) {
			if (aTimeSetting.isOn()) {
				this.registerTimeSettingAsOn(aTimeSetting);
			}
		}
	}

	/*
	 * We scheduel a new task that will be executed in the future some time
	 * later today
	 */
	private void scheduelNewStartTaskForTimeSettingInFutureLaterToday(
			TimeSetting aTimeSetting) {
		// we know this setting will take place in the fuuture so we need a task
		// that will be executed
		// between now and the time settings start time
		Long startMilli = aTimeSetting.getStartTimeAsDate().getTime();
		Long endMilli = aTimeSetting.getEndTimeAsDate().getTime();
		Long nowMilli = TimeSetting.getTimeNow().getTime();

		Long start = startMilli - nowMilli;
		if(start < 0) start = (long) 0;
		Long end = endMilli - startMilli;
		Log.d(TAG, start.toString());
		Log.d(TAG, "The task will start in " + start.toString()
				+ " milleseconds. from now");

		Log.d(TAG, "The task will end in " + end.toString()
				+ " milleseconds after started.");

		mStartTimerTask startTask = new mStartTimerTask(aTimeSetting, end);
		//cancel before comitting
		this.cancelTimeSetting(aTimeSetting);
		// keep a reference to this task just in case we want to cancel it
		// before it has started
		timeSettingStartTasks.put(aTimeSetting.getId(), startTask);
		Log.d(TAG,
				"Added time setting's start task with id:"
						+ aTimeSetting.getId() + "Size is now:"
						+ this.timeSettingStartTasks.size());
		this.aTimer.schedule(startTask, start);

	}

	/*
	 * we schedule a new start task that will run immediately
	 */

	private void scheduelNewStartTaskToRunNow(TimeSetting aTimeSetting) {
		Long nowMilli = TimeSetting.getTimeNow().getTime();
		Long endMilli = aTimeSetting.getEndTimeAsDate().getTime();

		Long end = endMilli - nowMilli;
		
		if(end < 0) end = (long) 0;

		mStartTimerTask startTask = new mStartTimerTask(aTimeSetting, end);
		// keep a reference to this task just in case we want to cancel it
		// before it has started
		
		//cancel before comitting
		this.cancelTimeSetting(aTimeSetting);
		timeSettingStartTasks.put(aTimeSetting.getId(), startTask);
		Log.d(TAG,
				"Added time setting's start task with id:"
						+ aTimeSetting.getId() + "Size is now:"
						+ this.timeSettingStartTasks.size());
		this.aTimer.schedule(startTask, 0);
	}

	public void broadcastTimeSettingEndedNotification() {
		Log.d(TAG, "broadcasting time setting ended!");
		Intent i = new Intent("com.exmaple.jajingprototype.system.event.TIME_SETTING_ENDED");
		context.sendBroadcast(i);

	}

	/*
	 * This task is responsible for turning jajajing on in X amount of
	 * milliseconds from now
	 */
	private class mStartTimerTask extends TimerTask {
		private static final String TAG = "mStartTimerTask";
		private volatile Looper mMyLooper;

		private Long end;
		private TimeSetting timeSetting;

		public mStartTimerTask(TimeSetting aTimeSetting, Long end) {

			this.end = end;
			this.timeSetting = aTimeSetting;

		}

		@Override
		public void run() {
			if (Looper.myLooper() == null) {
				Looper.prepare();
			}

			// start jajing
			String status = TimeSettingTaskManager.getInstance().user.getUserStatus().getAvailabilityStatus();
			if(status.equalsIgnoreCase("AVAILABLE")) status = timeSetting.status;
			TimeSettingTaskManager.this.user.goUnavailable(status, new AvailabilityTime(timeSetting.getEndTime()));
			// keep track of the task for this time setting
			//TimeSettingTaskManager.getInstance().timeSettingStartTasks.put(timeSetting.getId(), this);
			Log.d(TAG,
					"Added timesetting's start task with id:"
							+ timeSetting.getId()
							+ " to tasks. Size is now:"
							+ TimeSettingTaskManager.this.timeSettingStartTasks
									.size());
			// once the task has started we need to shut it down X milliseconds
			// from the start time to end time
			Log.d(TAG, "ending jajing in " + end.toString() + " milliseconds");
			// lets also remove the start task associated with the time setting from
			TimeSettingTaskManager.this.timeSettingStartTasks.remove(mStartTimerTask.this);
			// add an end task associated with this time setting
			mEndTaskTimerTask endTask = new mEndTaskTimerTask(timeSetting.getId());
			//check if we have already defined an end task for this time setting id
			TimeSettingTaskManager.this.cancelEndTask(timeSetting);
			TimeSettingTaskManager.this.timeSettingEndTasks.put(timeSetting.getId(), endTask);

			// schdule a new task end task to run, for jajing to stop
			TimeSettingTaskManager.getInstance().aTimer.schedule(endTask, end);

		}

		@Override
		public boolean cancel() {
			Log.d(TAG, "Called cancel on TimeTask start");
			return super.cancel();
			

		}

		private class mEndTaskTimerTask extends TimerTask {
			private volatile Looper mMyLooper;
			private Long timeSettingId;
			
			public mEndTaskTimerTask(Long timeSettingId) {
				this.timeSettingId = timeSettingId;
			}
			
			@Override
			public void run() {
				TimeSettingTaskManager taskManager = TimeSettingTaskManager.getInstance();
				taskManager.turnTimeSettingOff(timeSettingId);
				taskManager.broadcastTimeSettingEndedNotification();
				

			}
			
			@Override
			public boolean cancel() {
				Log.d(TAG, "Called cancel on TimeTask end");
				return super.cancel();
				

			}


		}

	}

}
