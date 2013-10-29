package com.wwc.jajing.activities;

import java.io.IOException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.wwc.jajing.R;
import com.wwc.jajing.cloud.contacts.CloudBackendAsync;
import com.wwc.jajing.cloud.contacts.CloudCallbackHandler;
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
	public static final String EXTRA_KEY_TIME_SETTING_ID = "time_setting_id";

	User user;
	TimeSetting timeSetting;

	TextView awayStatus;
	TextView availabilityTime;
	TextView notification;
	
	CloudBackendAsync m_cloudAsync ;
	Context mContext ;

    AudioManager audio;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_away);

        audio =  (AudioManager) getSystemService(Context.AUDIO_SERVICE);

		mContext = getApplicationContext() ;
		
		m_cloudAsync = new CloudBackendAsync(mContext);
		
		user = (User) JJSystemImpl.getInstance()
				.getSystemService(Services.USER);
		Log.d(TAG, TimeSettingTaskManager.getInstance()
				.getTimeSettingIdClosestToBeingDone().toString());
		timeSetting = TimeSetting.findById(TimeSetting.class,
				TimeSettingTaskManager.getInstance()
						.getTimeSettingIdClosestToBeingDone());
		awayStatus = (TextView) findViewById(R.id.textStatus);

		availabilityTime = (TextView) findViewById(R.id.textAvailabilityTime);
		notification = (TextView) findViewById(R.id.textNotification);

		awayStatus.setText(user.getUserStatus().getAvailabilityStatus());

		this.updateStatusOnCloud( user );
	}

	@Override
	protected void onStart() {
		super.onStart();

        audio.setRingerMode(AudioManager.RINGER_MODE_SILENT);

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
        this.user.goAvailable();
        CloudCallbackHandler<JSONObject> handler = new CloudCallbackHandler<JSONObject>() {
            @Override
            public void onComplete( JSONObject results ) {
                Toast.makeText( mContext , "Your status will be sent to those trying to reach you.", Toast.LENGTH_LONG ).show();
            }
            @Override
            public void onError( IOException exception ) {
                CloudBackendAsync.handleEndpointException( exception );
            }
        };
        //Change userId to actual user phone number
        long userId = 1l ;
        SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences( getApplicationContext() );
        userId = shared.getLong( "id" , 1l ) ;
        if( userId == 1l ) {
            Toast.makeText( this, "Not valid phone number for API calls", Toast.LENGTH_LONG ).show();
        }
        m_cloudAsync.pushStatusToCloud( userId , user , handler );

        Intent intent = new Intent(this, MissedLog.class);
        intent.putExtra("recentFlag", true);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
	}
	
	/**
	 * Asynchronously loads detached user contacts from cloud
	 * 
	 */
	private void updateStatusOnCloud( User user ) {

		CloudCallbackHandler<JSONObject> handler = new CloudCallbackHandler<JSONObject>() {
			@Override
			public void onComplete( JSONObject results ) {
				Toast.makeText( mContext , "Your status will be sent to those trying to reach you.", Toast.LENGTH_LONG ).show();
			}
			@Override
			public void onError( IOException exception ) {
				CloudBackendAsync.handleEndpointException( exception );
			}
		};
		//Change userId to actual user phone number
		long userId = 1l ;
		SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences( getApplicationContext() );
		userId = shared.getLong( "id" , 1l ) ;
		if( userId == 1l ) {
			Toast.makeText( this, "Not valid phone number for API calls", Toast.LENGTH_LONG ).show();
		}
		m_cloudAsync.pushStatusToCloud( userId , user , handler );
	}
	
}
