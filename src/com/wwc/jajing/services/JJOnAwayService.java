package com.wwc.jajing.services;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.wwc.jajing.R;
import com.wwc.jajing.activities.MainActivity;
import com.wwc.jajing.domain.entity.User;
import com.wwc.jajing.domain.services.CallManager;
import com.wwc.jajing.listeners.CSLonAvailable;
import com.wwc.jajing.listeners.CSLonUnavailable;
import com.wwc.jajing.system.JJSystem;
import com.wwc.jajing.system.JJSystemImpl;
import com.wwc.jajing.system.JJSystemImpl.Services;

/*
 * This is the service that should run on the background when the user DOES NOT WANT TO BE DISTURBED.
 * 
 * This service is responsible for:
 * -- activating a "onCallStateListener" that will listen for incoming phone calls.
 * -- silencing the phone
 * 
 * 
 * 
 * 
 */
public class JJOnAwayService extends Service {



	private static final String TAG = "JJOnAwayService";
	private TelephonyManager tm;
	private CallManager cm;
	
	private PhoneStateListener cslOnUnavailble;
	private PhoneStateListener cslOnAvailble;

	private User user;
	

	public JJOnAwayService()
	{
		super();
		//CACHE SYSTEM FOR PERFORMANCE
		JJSystem jjSystem = JJSystemImpl.getInstance();
		this.cm = (CallManager) jjSystem.getSystemService(Services.CALL_MANAGER);
		this.user = (User) jjSystem.getSystemService(Services.USER);
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		Log.d(TAG, "service is no longer bound to AwayActivity.");

		return super.onUnbind(intent);
	}
	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		this.startAwayForegruondService();
		// We want this service to stop running if it gets shut down b/c of memory problems
		return START_NOT_STICKY;
	}
	
	@SuppressLint("NewApi")
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private void startAwayForegruondService()
	{
		/*Notification notification = new Notification.Builder(this).setContentTitle("Unavailable")
				.setSmallIcon(R.drawable.logo).build();
		startForeground(1, notification);*/
	}


	/*
	 * The system calls this method when the service is first created, to
	 * perform one-time setup procedures
	 * 
	 * 
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "JJOnAwayService service created");
		
		//if the user's is avaialable when this service is created, set him to unavailable
		if(this.user.isAvailable()) this.user.goUnavailable(this.user.getUserStatus().getAvailabilityStatus(), this.user.getAvailabilityTime());
			
		this.registerCSLonUnavailable();
		this.unregisterCSLonAvailable();
		

	}
	
	public void registerCSLonUnavailable()
	{

		tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
		this.cslOnUnavailble = new CSLonUnavailable();
		tm.listen(this.cslOnUnavailble, PhoneStateListener.LISTEN_CALL_STATE);
		Log.d(TAG, "CSLonUnavailable registered");

	}
	
	public void registerCSLonAvailable()
	{

		tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
		this.cslOnAvailble = new CSLonAvailable();
		tm.listen(this.cslOnAvailble, PhoneStateListener.LISTEN_CALL_STATE);
		Log.d(TAG, "CSLonAvailable registered");

	}
	
	public void unregisterCSLonAvailable()
	{

		tm.listen(this.cslOnAvailble, PhoneStateListener.LISTEN_NONE);
		Log.d(TAG, "CSLonAvailable unregistered");

	}
	
	public void unregisterCSLonUnavailable()
	{

		tm.listen(this.cslOnUnavailble, PhoneStateListener.LISTEN_NONE);
		Log.d(TAG, "CSLonUnavailable unregistered");

	}

	/*
	 * Service should implement this to clean up any resources such as threads,
	 * registered listeners, receivers, etc. This is the last call the service
	 * receives. Called when service is no longer in use.
	 * 
	 *
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		// unregister our receivers

		// unregister our callStateListeners
		this.unregisterCSLonUnavailable();
		this.registerCSLonAvailable();

		// restore phone's ring volume
		this.cm.silenceRinger(false);
		
		//deliver text messages that were missed
		this.cm.getRecentMissedMessageLog().deliverRecentMissedMessagesToInbox();
		
		//set user's availability status to available
		user.goAvailable();
		this.updateDashboardAvailabilityStatus();
		
		Log.d(TAG, "JJOnAwayService Destroyed.");
		
		stopForeground(true);


	}
	
	private void updateDashboardAvailabilityStatus()
	{
		Intent intent = new Intent(MainActivity.DASHBOARD_INTENT);
		intent.putExtra("status", this.user.getUserStatus().getAvailabilityStatus());
		this.sendBroadcast(intent);
		Log.d(TAG, "Dashboard Notification sent to update availability status!");
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	
	
	
	
	
	
	
	
	
	

}
