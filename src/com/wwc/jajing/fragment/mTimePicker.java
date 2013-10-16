package com.wwc.jajing.fragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TimePicker;
import com.wwc.jajing.activities.AwayOptions;
import com.wwc.jajing.activities.MainActivity;
import com.wwc.jajing.settings.time.TimeSettingTaskManager;

@SuppressLint("ValidFragment")
@TargetApi(Build.VERSION_CODES.FROYO)
public class mTimePicker extends DialogFragment implements
		OnTimeSetListener {


	private static final String TAG  = "mTimePicker";
	private boolean isTimePickerForStartTime;
	private boolean isCalled = false;

	public mTimePicker(boolean isTimePickerForStartTime) {
		this.isTimePickerForStartTime = isTimePickerForStartTime;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		// Use the current time as the default values for the picker
		final Calendar c = Calendar.getInstance();
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);

		TimePickerDialog tpd = new TimePickerDialog(getActivity(), this,
				hour, minute, DateFormat.is24HourFormat(getActivity()));
		setTimePickerTitle(tpd);

		// Create a new instance of TimePickerDialog and return it
		return tpd;
	}

	private void setTimePickerTitle(TimePickerDialog tpd) {
		if (isTimePickerForStartTime) {
			tpd.setTitle("Start");
		} else {
			tpd.setTitle("End");
		}
	}

	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

		if(isCalled == false) {
			setTime(hourOfDay, minute);
			isCalled = true;
			Log.d(TAG, "onTimeSet Called" + hourOfDay + ": " + minute);
		}
			

		
	}

	private void setTime(int hourOfDay, int minute) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, hourOfDay);
		// instead of c.set(Calendar.HOUR, hour);
		c.set(Calendar.MINUTE, minute);

		SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");

		if (isTimePickerForStartTime) {
			// cancel time setting if one is set
			TimeSettingTaskManager.getInstance().turnTimeSettingOff(1L);

			// set the start time
			MainActivity.getInstance().setStartTime(sdf.format(c.getTime()));
			Log.d("SA", "setting start time.");

		} else {
			// set the end time
			MainActivity.getInstance().setEndTime(sdf.format(c.getTime()));
			Log.d("SA", "setting end time.");

		}

	}

}