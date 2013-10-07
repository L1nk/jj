package com.wwc.jajing.domain.entity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Looper;
import android.util.Log;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;
import com.wwc.jajing.domain.value.AvailabilityTime;
import com.wwc.jajing.domain.value.PhoneNumber;
import com.wwc.jajing.domain.value.UserStatus;
import com.wwc.jajing.permissions.PermissionManager;
import com.wwc.jajing.permissions.PermissionManager.Permissions;
import com.wwc.jajing.services.JJOnAwayService;
import com.wwc.jajing.settings.time.TimeSettingTaskManager;
import com.wwc.jajing.system.JJSystemImpl;
import com.wwc.jajing.system.JJSystemImpl.Services;

/*
 * Represents the User.
 * 
 * Singleton. DO NOT  NEW THIS CLASS UP...
 * you can retreive the instance thorugh the system's registry.
 * 
 * 
 */

public class UserImpl extends SugarRecord implements User {

	private static final String TAG = "UserImpl";
	private Long id;
	private String availabilityStatus = AVAILAILBLE;
	private String availabilityTime;
	@Ignore
	public static SimpleDateFormat fullDateTimeFormatter = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSSZ" );
	@Ignore
	private Map<String,String> userProfile = new HashMap<String, String>();
	
	@Ignore
	private mEndTaskTimerTask currentEndTask;
	
	@Ignore
	private boolean isMakingCall;
	
	@Ignore 
	private AvailabilityTime availTime;

	@Ignore
	public static final String AVAILAILBLE = "AVAILABLE";

	@Ignore
	private Context context;

	@Ignore
	private static final UserImpl instance = new UserImpl(
			(Context) JJSystemImpl.getInstance().getSystemService(
					Services.CONTEXT));
	@Ignore
	private String status = "" ;

	@Ignore
	private PermissionManager pm = null;

	// constructor left public because ORM requires this to be public
	public UserImpl(Context context) {
		super(context);
		this.context = context;
		
	}

	public static UserImpl getInstance() {
		return instance;
	}

	public Long getId() {
		return this.id;
	}

	@Override
	public void makeCall(PhoneNumber phoneNumber) {
		this.setIsMakingCall(true);
		Intent callIntent = new Intent(Intent.ACTION_CALL);
		callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		callIntent.setData(Uri.parse("tel:" + phoneNumber.toString()));
		context.startActivity(callIntent);
	}
	
	public boolean isMakingCall()
	{
		return this.isMakingCall;
	}
	
	public void setIsMakingCall(boolean isMakingCall)
	{
		this.isMakingCall = isMakingCall;
		Log.d(TAG, "User is now making a new call. Field:ismakingcall was set to:" + isMakingCall);
	}
	
	
	@Override
	public boolean goUnavailable(String aReason, String aStartTime, AvailabilityTime anAvailabilityTime) {
		this.setAvailabilityStatus(aReason);
		this.setAvailabilityTime(anAvailabilityTime);
		this.save();
		
		int dayOfTheWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);

		String start = TimeSetting.dateFormatterTIME.format(TimeSetting.getDateOfTimeString(aStartTime));
		if(!TimeSetting.isValidTimeInterval(start, anAvailabilityTime.getAvailabilityTimeString())) { //check to make sure its a valid interval
			//Toast.makeText(context, "invalid interval", Toast.LENGTH_SHORT).show();
			return false;
		}
		

		TimeSetting mainTimeSetting = TimeSetting.findById(TimeSetting.class, 1L);
		mainTimeSetting.setStartTime(start);
		mainTimeSetting.setEndTime(anAvailabilityTime.getAvailabilityTimeString());
		Log.d(TAG, "setting start time:" + start);
		Log.d(TAG, "setting end time:" + anAvailabilityTime.getAvailabilityTimeString());
		mainTimeSetting.save();
		TimeSettingTaskManager taskManager = TimeSettingTaskManager.getInstance();
		taskManager.turnTimeSettingOn(1L);
		return true;

	}
	
	

	/*
	 * Time setting activity, will go unavailable immediately and be set to return in the future
	 * The start time is now.
	 * 
	 */
	public void goUnavailable(String aReason, AvailabilityTime aTimeWillBeBack) {
		this.setAvailabilityStatus(aReason);
		this.setAvailabilityTime(aTimeWillBeBack);
		this.save();
		
		int dayOfTheWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
		
		String now = TimeSetting.dateFormatterTIME.format(TimeSetting.getTimeNow());
	
		if(!TimeSetting.isValidTimeInterval(now, aTimeWillBeBack.getAvailabilityTimeString())) return;
		
		this.startJJOnAwayService();

		//schdule a new task end task to run, for jajing to stop
		Long nowMilli = TimeSetting.getTimeNow().getTime();
		Long endMilli = aTimeWillBeBack.getTime();
		Log.d(TAG,"endMilli" + endMilli );
		Long end = endMilli - nowMilli;
		

		//keep a reference to this task just in case users changes his availability time
		mEndTaskTimerTask pendingTask = new mEndTaskTimerTask(); 
		if(this.currentEndTask != null){
			//cancel current task
			this.currentEndTask.cancel();
			Log.d(TAG, "current end task cancelled");
			
		} 
		this.currentEndTask = pendingTask;
		TimeSettingTaskManager.getInstance().aTimer.schedule(this.currentEndTask, end);
		
	}
	
	private class mEndTaskTimerTask extends TimerTask
	{
		private volatile Looper mMyLooper;

		@Override
		public void run() {
			
			
		}
		
	}
	
	
	private void setAvailabilityTime(AvailabilityTime anAvailabilityTime) {
		this.availTime = anAvailabilityTime;
		this.availabilityTime = anAvailabilityTime.getAvailabilityTimeString();
		Log.d(TAG, "availability time is now: " + this.availabilityTime);
	}
	
	private void startJJOnAwayService()
	{
		Intent service = new Intent(context, JJOnAwayService.class);
		context.startService(service); 
	}

	/*
	 * 
	 * 
	 */
	@Override
	public void goAvailable() {
		this.setAvailabilityStatus(this.AVAILAILBLE);
		
		//TODO - may cause bug issue here...
		this.availabilityTime = "";
		this.save();
		this.stopJJOnAwayService();
		
		if(TimeSetting.findById(TimeSetting.class, 1L) != null){
			TimeSettingTaskManager.getInstance().turnTimeSettingOff(1L);

		}
		
	}
	
	
	private void stopJJOnAwayService() {
		Intent intent = new Intent(context, JJOnAwayService.class);
		context.stopService(intent);
	}
	

	public UserStatus getUserStatus() {
		if(this.availTime != null) {
			UserStatus us = new UserStatus(availabilityStatus, this.availTime.getAvailabilityTimeString());
			return us;
		} else {
			//FIXED BUG, app would not start if user status was null
			UserStatus us = new UserStatus(availabilityStatus, "Unknown");
			return us;

		}
	
	}

	private void setAvailabilityStatus(String status) {
		this.availabilityStatus = status;
		this.save();

	}



	@Override
	public boolean isAvailable() {
		if(this.availabilityStatus == null) {
			throw new IllegalStateException("Availability status cannot be null!");
		}
		
		if (this.availabilityStatus.equals(this.AVAILAILBLE))
			return true;
		else
			return false;
	}

	/*
	 * Delegates to Permission Manager
	 *
	 */
	@Override
	public void givePermission(Caller aPermissable,
			Permissions aPermission) {
		if(aPermissable == null) throw new IllegalArgumentException("Caller cannot be null. Please make sure caller is persisted before giving permissions to him.");
		PermissionManager pm = this.getPermissionManager();
		pm.attachPermission(aPermissable, pm.getPermission(aPermission), true);
		
		Log.d(TAG, "Permission has be granted!" + aPermission.toString());

	}
	
	/*
	 * Delegates to Permission Manager
	 * 
	 *
	 */
	@Override
	public void denyPermission(Caller aPermissable,
			Permissions aPermission)
	{
		if(aPermissable == null) throw new IllegalArgumentException("Caller cannot be null. Please make sure caller is persisted before removing permissions from him.");
		PermissionManager pm = this.getPermissionManager();
		pm.attachPermission( aPermissable, pm.getPermission(aPermission), false);
		Log.d(TAG, "Permission has be revoked!" + aPermission.toString());

		
	}
	
	private PermissionManager getPermissionManager() //lazy load permission manager
	{
		if (this.pm == null) {
			this.pm = PermissionManager.getInstance();
			return this.pm;
		}
		return this.pm;
	}
	
	private boolean isJJOnAwayServiceRunning() {
	    ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (JJOnAwayService.class.getName().equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}

	@Override
	public AvailabilityTime getAvailabilityTime() {
		if(this.availabilityTime == null || this.availabilityTime.equalsIgnoreCase("")) {
			int dayOfTheWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
			//Days today = Days.values()[dayOfTheWeek - 1];
			Long nowMilli = TimeSetting.getTimeNow().getTime();
			Long pastMilli = nowMilli - 5000;
			Date pastDate = new Date();
			pastDate.setTime(pastMilli);
						
			String now = TimeSetting.dateFormatterTIME.format(pastDate);
			this.availTime = new AvailabilityTime(now);
			Log.d(TAG, this.availTime.getAvailabilityTimeString());
			Log.d(TAG, "availabiity time was not set, setting default availability time...");
			return  this.availTime;
		}
		Log.d(TAG, "availability Time is :" + this.availabilityTime);
		AvailabilityTime anAvailabilityTime = new AvailabilityTime(this.availabilityTime);
		this.setAvailabilityTime(anAvailabilityTime);
		return this.availTime;
	}

	@Override
	public String getFullStartDateTime() {
		return fullDateTimeFormatter.format( new Date() );
	}

	@SuppressWarnings( "deprecation" )
	@Override
	public String getFullEndDateTime() {
		Date endTime = new Date() ;
		int minute = (int) (availTime.getTime() / (1000 * 60)) % 60;
		int hour = (int) (availTime.getTime() / (1000 * 60 * 60)) % 24;
		endTime.setHours( hour );
		endTime.setMinutes( minute );
		return fullDateTimeFormatter.format( endTime );
	}

}
