package com.wwc.jajing.fragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import com.wwc.jajing.activities.TimeSettings;

public class EndTimeTimePickerFragment extends DialogFragment implements OnTimeSetListener {
	
private static final String TAG = "StartTimeTimePickerFragment";
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog tpd = new TimePickerDialog(getActivity(), this, hour, minute,
        DateFormat.is24HourFormat(getActivity()));
        
        tpd.setTitle("end time");
        
        // Create a new instance of TimePickerDialog and return it
        return tpd;
    }

	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
    	
    	
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        //instead of c.set(Calendar.HOUR, hour);
        c.set(Calendar.MINUTE, minute);
        
        TimeSettings timeSettingsActivity = ((TimeSettings) getActivity());
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        timeSettingsActivity.setEndTime(sdf.format(c.getTime()));
        
    }
   
    
}
