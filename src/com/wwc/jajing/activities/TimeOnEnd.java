package com.wwc.jajing.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import com.wwc.jajing.R;
import com.wwc.jajing.domain.entity.User;
import com.wwc.jajing.services.JJOnAwayService;
import com.wwc.jajing.system.JJSystemImpl;
import com.wwc.jajing.system.JJSystemImpl.Services;

/*
 * This is what the user will see when a time setting is ended and the user is unavailable
 */
public class TimeOnEnd extends Activity {

	private User user = (User) JJSystemImpl.getInstance().getSystemService(
			Services.USER);

    AudioManager audio =  (AudioManager) getSystemService(Context.AUDIO_SERVICE);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("your time setting is up. do you want to go available?");
		builder.setTitle("Time Setting Alert");

		builder.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
                        audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
						// User clicked OK button
						TimeOnEnd.this.user.goAvailable();
						TimeOnEnd.this.finish();
						// show his recent history
						Intent intent = new Intent(TimeOnEnd.this,
								MissedLog.class);
						intent.putExtra("recentFlag", true);
						startActivity(intent);
						refreshTimeSettings();
					}
				});
		builder.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// User clicked Cancel button
						TimeOnEnd.this.finish();
						refreshTimeSettings();
					}
				});

		AlertDialog dialog = builder.create();
		dialog.show();
	}
	
	/*
	 * This method doesn't actually do any time setting manipulation.
	 * It redirect the user to the my time settings page so it can fresh itself. then it closes that page out.
	 * Veryy hacky solution.
	 */
	private void refreshTimeSettings()
	{
		Intent i = new Intent(this, MyTimeSettings.class);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		i.putExtra(MyTimeSettings.EXTRA_REFRSH_AND_CLOSE, true);
		this.startActivity(i);
		
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	private void stopJJOnAwayService() {
		Intent intent = new Intent(this, JJOnAwayService.class);
		this.stopService(intent);
	}

}
